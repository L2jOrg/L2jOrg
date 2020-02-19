package org.l2j.gameserver.instancemanager;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.data.xml.impl.SpawnsData;
import org.l2j.gameserver.datatables.SpawnTable;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.spawns.NpcSpawnTemplate;
import org.l2j.gameserver.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;


/**
 * Database spawn manager.
 *
 * @author godson, UnAfraid
 */
public class DBSpawnManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBSpawnManager.class);

    protected final Map<Integer, Npc> _npcs = new ConcurrentHashMap<>();
    protected final Map<Integer, Spawn> _spawns = new ConcurrentHashMap<>();
    protected final Map<Integer, StatsSet> _storedInfo = new ConcurrentHashMap<>();
    protected final Map<Integer, ScheduledFuture<?>> _schedules = new ConcurrentHashMap<>();

    private DBSpawnManager() {
        load();
    }

    /**
     * Load.
     */
    public void load() {
        if (Config.ALT_DEV_NO_SPAWNS) {
            return;
        }

        _npcs.clear();
        _spawns.clear();
        _storedInfo.clear();
        _schedules.clear();

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT * FROM npc_respawns");
             ResultSet rset = statement.executeQuery()) {
            while (rset.next()) {
                final NpcTemplate template = getValidTemplate(rset.getInt("id"));
                if (template != null) {
                    final Spawn spawn = new Spawn(template);
                    spawn.setXYZ(rset.getInt("x"), rset.getInt("y"), rset.getInt("z"));
                    spawn.setAmount(1);
                    spawn.setHeading(rset.getInt("heading"));

                    final List<NpcSpawnTemplate> spawns = SpawnsData.getInstance().getNpcSpawns(npc -> (npc.getId() == template.getId()) && npc.hasDBSave());
                    if (spawns.isEmpty()) {
                        LOGGER.warn(": Couldn't find spawn declaration for npc: " + template.getId() + " - " + template.getName());
                        deleteSpawn(spawn, true);
                        continue;
                    } else if (spawns.size() > 1) {
                        LOGGER.warn(": Found multiple database spawns for npc: " + template.getId() + " - " + template.getName() + " " + spawns);
                        continue;
                    }

                    final NpcSpawnTemplate spawnTemplate = spawns.get(0);
                    spawn.setSpawnTemplate(spawnTemplate);

                    int respawn = 0;
                    int respawnRandom = 0;
                    if (spawnTemplate.getRespawnTime() != null) {
                        respawn = (int) spawnTemplate.getRespawnTime().getSeconds();
                    }
                    if (spawnTemplate.getRespawnTimeRandom() != null) {
                        respawnRandom = (int) spawnTemplate.getRespawnTimeRandom().getSeconds();
                    }

                    if (respawn > 0) {
                        spawn.setRespawnDelay(respawn, respawnRandom);
                        spawn.startRespawn();
                    } else {
                        spawn.stopRespawn();
                        LOGGER.warn("Found database spawns without respawn for npc: " + template.getId() + " - " + template.getName() + " " + spawnTemplate);
                        continue;
                    }

                    addNewSpawn(spawn, rset.getLong("respawnTime"), rset.getDouble("currentHp"), rset.getDouble("currentMp"), false);
                } else {
                    LOGGER.warn(": Could not load npc #" + rset.getInt("id") + " from DB");
                }
            }

            LOGGER.info(getClass().getSimpleName() + ": Loaded " + _npcs.size() + " Instances");
            LOGGER.info(getClass().getSimpleName() + ": Scheduled " + _schedules.size() + " Instances");
        } catch (SQLException e) {
            LOGGER.warn(getClass().getSimpleName() + ": Couldnt load npc_respawns table", e);
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Error while initializing DBSpawnManager: ", e);
        }
    }

    private void scheduleSpawn(int npcId) {
        final Npc npc = _spawns.get(npcId).doSpawn();
        if (npc != null) {
            npc.setRaidBossStatus(RaidBossStatus.ALIVE);

            final StatsSet info = new StatsSet();
            info.set("currentHP", npc.getCurrentHp());
            info.set("currentMP", npc.getCurrentMp());
            info.set("respawnTime", 0);

            _storedInfo.put(npcId, info);
            _npcs.put(npcId, npc);
            LOGGER.info(getClass().getSimpleName() + ": Spawning NPC " + npc.getName());
        }

        _schedules.remove(npcId);
    }

    /**
     * Update status.
     *
     * @param npc       the npc
     * @param isNpcDead the is npc dead
     */
    public void updateStatus(Npc npc, boolean isNpcDead) {
        final StatsSet info = _storedInfo.get(npc.getId());
        if (info == null) {
            return;
        }

        if (isNpcDead) {
            npc.setRaidBossStatus(RaidBossStatus.DEAD);

            final int respawnMinDelay = (int) (npc.getSpawn().getRespawnMinDelay() * Config.RAID_MIN_RESPAWN_MULTIPLIER);
            final int respawnMaxDelay = (int) (npc.getSpawn().getRespawnMaxDelay() * Config.RAID_MAX_RESPAWN_MULTIPLIER);
            final int respawnDelay = Rnd.get(respawnMinDelay, respawnMaxDelay);
            final long respawnTime = System.currentTimeMillis() + respawnDelay;

            info.set("currentHP", npc.getMaxHp());
            info.set("currentMP", npc.getMaxMp());
            info.set("respawnTime", respawnTime);

            if (!_schedules.containsKey(npc.getId()) && ((respawnMinDelay > 0) || (respawnMaxDelay > 0))) {
                LOGGER.info(getClass().getSimpleName() + ": Updated " + npc.getName() + " respawn time to " + GameUtils.formatDate(new Date(respawnTime), "dd.MM.yyyy HH:mm"));

                _schedules.put(npc.getId(), ThreadPool.schedule(() -> scheduleSpawn(npc.getId()), respawnDelay));
                updateDb();
            }
        } else {
            npc.setRaidBossStatus(RaidBossStatus.ALIVE);

            info.set("currentHP", npc.getCurrentHp());
            info.set("currentMP", npc.getCurrentMp());
            info.set("respawnTime", 0);
        }
        _storedInfo.put(npc.getId(), info);
    }

    /**
     * Adds the new spawn.
     *
     * @param spawn       the spawn dat
     * @param respawnTime the respawn time
     * @param currentHP   the current hp
     * @param currentMP   the current mp
     * @param storeInDb   the store in db
     */
    public void addNewSpawn(Spawn spawn, long respawnTime, double currentHP, double currentMP, boolean storeInDb) {
        if (spawn == null) {
            return;
        }
        if (_spawns.containsKey(spawn.getId())) {
            return;
        }

        final int npcId = spawn.getId();
        final long time = System.currentTimeMillis();

        SpawnTable.getInstance().addNewSpawn(spawn, false);

        if ((respawnTime == 0) || (time > respawnTime)) {
            final Npc npc = spawn.doSpawn();
            if (npc != null) {
                npc.setCurrentHp(currentHP);
                npc.setCurrentMp(currentMP);
                npc.setRaidBossStatus(RaidBossStatus.ALIVE);

                _npcs.put(npcId, npc);

                final StatsSet info = new StatsSet();
                info.set("currentHP", currentHP);
                info.set("currentMP", currentMP);
                info.set("respawnTime", 0);

                _storedInfo.put(npcId, info);
            }
        } else {
            final long spawnTime = respawnTime - System.currentTimeMillis();
            _schedules.put(npcId, ThreadPool.schedule(() -> scheduleSpawn(npcId), spawnTime));
        }

        _spawns.put(npcId, spawn);

        if (storeInDb) {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement statement = con.prepareStatement("INSERT INTO npc_respawns (id, x, y, z, heading, respawnTime, currentHp, currentMp) VALUES(?, ?, ?, ?, ?, ?, ?, ?)")) {
                statement.setInt(1, spawn.getId());
                statement.setInt(2, spawn.getX());
                statement.setInt(3, spawn.getY());
                statement.setInt(4, spawn.getZ());
                statement.setInt(5, spawn.getHeading());
                statement.setLong(6, respawnTime);
                statement.setDouble(7, currentHP);
                statement.setDouble(8, currentMP);
                statement.execute();
            } catch (Exception e) {
                // problem with storing spawn
                LOGGER.warn(getClass().getSimpleName() + ": Could not store npc #" + npcId + " in the DB: ", e);
            }
        }
    }

    public Npc addNewSpawn(Spawn spawn, boolean storeInDb) {
        if (spawn == null) {
            return null;
        }

        final int npcId = spawn.getId();
        final Spawn existingSpawn = _spawns.get(npcId);
        if (existingSpawn != null) {
            return existingSpawn.getLastSpawn();
        }

        SpawnTable.getInstance().addNewSpawn(spawn, false);

        final Npc npc = spawn.doSpawn();
        if (npc == null) {
            throw new NullPointerException();
        }
        npc.setRaidBossStatus(RaidBossStatus.ALIVE);

        final StatsSet info = new StatsSet();
        info.set("currentHP", npc.getMaxHp());
        info.set("currentMP", npc.getMaxMp());
        info.set("respawnTime", 0);

        _npcs.put(npcId, npc);
        _storedInfo.put(npcId, info);

        _spawns.put(npcId, spawn);

        if (storeInDb) {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement statement = con.prepareStatement("INSERT INTO npc_respawns (id, x, y, z, heading, respawnTime, currentHp, currentMp) VALUES(?, ?, ?, ?, ?, ?, ?, ?)")) {
                statement.setInt(1, spawn.getId());
                statement.setInt(2, spawn.getX());
                statement.setInt(3, spawn.getY());
                statement.setInt(4, spawn.getZ());
                statement.setInt(5, spawn.getHeading());
                statement.setLong(6, 0);
                statement.setDouble(7, npc.getMaxHp());
                statement.setDouble(8, npc.getMaxMp());
                statement.execute();
            } catch (Exception e) {
                // problem with storing spawn
                LOGGER.warn(getClass().getSimpleName() + ": Could not store npc #" + npcId + " in the DB: ", e);
            }
        }

        return npc;
    }

    /**
     * Delete spawn.
     *
     * @param spawn    the spawn dat
     * @param updateDb the update db
     */
    public void deleteSpawn(Spawn spawn, boolean updateDb) {
        if (spawn == null) {
            return;
        }

        final int npcId = spawn.getId();

        _spawns.remove(npcId);
        _npcs.remove(npcId);
        _storedInfo.remove(npcId);

        final ScheduledFuture<?> task = _schedules.remove(npcId);
        if (task != null) {
            task.cancel(true);
        }

        if (updateDb) {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement ps = con.prepareStatement("DELETE FROM npc_respawns WHERE id = ?")) {
                ps.setInt(1, npcId);
                ps.execute();
            } catch (Exception e) {
                // problem with deleting spawn
                LOGGER.warn(getClass().getSimpleName() + ": Could not remove npc #" + npcId + " from DB: ", e);
            }
        }

        SpawnTable.getInstance().deleteSpawn(spawn, false);
    }

    /**
     * Update database.
     */
    private void updateDb() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE npc_respawns SET respawnTime = ?, currentHP = ?, currentMP = ? WHERE id = ?")) {
            for (Integer npcId : _storedInfo.keySet()) {
                if (npcId == null) {
                    continue;
                }

                final Npc npc = _npcs.get(npcId);
                if (npc == null) {
                    continue;
                }

                if (npc.getRaidBossStatus() == RaidBossStatus.ALIVE) {
                    updateStatus(npc, false);
                }

                final StatsSet info = _storedInfo.get(npcId);
                if (info == null) {
                    continue;
                }

                try {
                    statement.setLong(1, info.getLong("respawnTime"));
                    statement.setDouble(2, info.getDouble("currentHP"));
                    statement.setDouble(3, info.getDouble("currentMP"));
                    statement.setInt(4, npcId);
                    statement.executeUpdate();
                    statement.clearParameters();
                } catch (SQLException e) {
                    LOGGER.warn(getClass().getSimpleName() + ": Couldnt update npc_respawns table ", e);
                }
            }
        } catch (SQLException e) {
            LOGGER.warn(getClass().getSimpleName() + ": SQL error while updating database spawn to database: ", e);
        }
    }

    /**
     * Gets the all npc status.
     *
     * @return the all npc status
     */
    public String[] getAllNpcsStatus() {
        final String[] msg = new String[(_npcs == null) ? 0 : _npcs.size()];

        if (_npcs == null) {
            msg[0] = "None";
            return msg;
        }

        int index = 0;

        for (int i : _npcs.keySet()) {
            final Npc npc = _npcs.get(i);
            msg[index++] = npc.getName() + ": " + npc.getRaidBossStatus().name();
        }

        return msg;
    }

    /**
     * Gets the npc status.
     *
     * @param npcId the npc id
     * @return the raid npc status
     */
    public String getNpcsStatus(int npcId) {
        String msg = "NPC Status..." + System.lineSeparator();

        if (_npcs == null) {
            msg += "None";
            return msg;
        }

        if (_npcs.containsKey(npcId)) {
            final Npc npc = _npcs.get(npcId);

            msg += npc.getName() + ": " + npc.getRaidBossStatus().name();
        }

        return msg;
    }

    /**
     * Gets the raid npc status id.
     *
     * @param npcId the npc id
     * @return the raid npc status id
     */
    public RaidBossStatus getNpcStatusId(int npcId) {
        if (_npcs.containsKey(npcId)) {
            return _npcs.get(npcId).getRaidBossStatus();
        } else if (_schedules.containsKey(npcId)) {
            return RaidBossStatus.DEAD;
        } else {
            return RaidBossStatus.UNDEFINED;
        }
    }

    /**
     * Gets the valid template.
     *
     * @param npcId the npc id
     * @return the valid template
     */
    public NpcTemplate getValidTemplate(int npcId) {
        return NpcData.getInstance().getTemplate(npcId);
    }

    /**
     * Notify spawn night npc.
     *
     * @param npc the npc
     */
    public void notifySpawnNightNpc(Npc npc) {
        final StatsSet info = new StatsSet();
        info.set("currentHP", npc.getCurrentHp());
        info.set("currentMP", npc.getCurrentMp());
        info.set("respawnTime", 0);

        npc.setRaidBossStatus(RaidBossStatus.ALIVE);

        _storedInfo.put(npc.getId(), info);
        _npcs.put(npc.getId(), npc);
    }

    /**
     * Checks if the npc is defined.
     *
     * @param npcId the npc id
     * @return {@code true} if is defined
     */
    public boolean isDefined(int npcId) {
        return _spawns.containsKey(npcId);
    }

    /**
     * Gets the npcs.
     *
     * @return the npcs
     */
    public Map<Integer, Npc> getNpcs() {
        return _npcs;
    }

    /**
     * Gets the spawns.
     *
     * @return the spawns
     */
    public Map<Integer, Spawn> getSpawns() {
        return _spawns;
    }

    /**
     * Gets the stored info.
     *
     * @return the stored info
     */
    public Map<Integer, StatsSet> getStoredInfo() {
        return _storedInfo;
    }

    /**
     * Saves and clears the raid npces status, including all schedules.
     */
    public void cleanUp() {
        updateDb();

        _npcs.clear();

        if (_schedules != null) {
            for (Integer npcId : _schedules.keySet()) {
                final ScheduledFuture<?> f = _schedules.get(npcId);
                f.cancel(true);
            }
            _schedules.clear();
        }

        _storedInfo.clear();
        _spawns.clear();
    }

    public static DBSpawnManager getInstance() {
        return Singleton.INSTANCE;
    }
    private static class Singleton {
        private static final DBSpawnManager INSTANCE = new DBSpawnManager();
    }
}
