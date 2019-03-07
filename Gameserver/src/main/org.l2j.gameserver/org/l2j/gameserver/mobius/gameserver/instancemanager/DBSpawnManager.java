package org.l2j.gameserver.mobius.gameserver.instancemanager;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.mobius.gameserver.data.xml.impl.SpawnsData;
import org.l2j.gameserver.mobius.gameserver.datatables.SpawnTable;
import org.l2j.gameserver.mobius.gameserver.model.L2Spawn;
import org.l2j.gameserver.mobius.gameserver.model.StatsSet;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.mobius.gameserver.model.actor.templates.L2NpcTemplate;
import org.l2j.gameserver.mobius.gameserver.model.spawns.NpcSpawnTemplate;
import org.l2j.gameserver.mobius.gameserver.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database spawn manager.
 * @author godson, UnAfraid
 */
public class DBSpawnManager
{
    private static final Logger LOGGER = Logger.getLogger(DBSpawnManager.class.getName());

    protected final Map<Integer, L2Npc> _npcs = new ConcurrentHashMap<>();
    protected final Map<Integer, L2Spawn> _spawns = new ConcurrentHashMap<>();
    protected final Map<Integer, StatsSet> _storedInfo = new ConcurrentHashMap<>();
    protected final Map<Integer, ScheduledFuture<?>> _schedules = new ConcurrentHashMap<>();

    public enum DBStatusType
    {
        ALIVE,
        DEAD,
        UNDEFINED
    }

    /**
     * Instantiates a new raid npc spawn manager.
     */
    protected DBSpawnManager()
    {
        load();
    }

    /**
     * Load.
     */
    public void load()
    {
        if (Config.ALT_DEV_NO_SPAWNS)
        {
            return;
        }

        _npcs.clear();
        _spawns.clear();
        _storedInfo.clear();
        _schedules.clear();

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT * FROM npc_respawns");
             ResultSet rset = statement.executeQuery())
        {
            while (rset.next())
            {
                final L2NpcTemplate template = getValidTemplate(rset.getInt("id"));
                if (template != null)
                {
                    final L2Spawn spawn = new L2Spawn(template);
                    spawn.setXYZ(rset.getInt("x"), rset.getInt("y"), rset.getInt("z"));
                    spawn.setAmount(1);
                    spawn.setHeading(rset.getInt("heading"));

                    final List<NpcSpawnTemplate> spawns = SpawnsData.getInstance().getNpcSpawns(npc -> (npc.getId() == template.getId()) && npc.hasDBSave());
                    if (spawns.isEmpty())
                    {
                        LOGGER.warning(getClass().getSimpleName() + ": Couldn't find spawn declaration for npc: " + template.getId() + " - " + template.getName());
                        deleteSpawn(spawn, true);
                        continue;
                    }
                    else if (spawns.size() > 1)
                    {
                        LOGGER.warning(getClass().getSimpleName() + ": Found multiple database spawns for npc: " + template.getId() + " - " + template.getName() + " " + spawns);
                        continue;
                    }

                    final NpcSpawnTemplate spawnTemplate = spawns.get(0);
                    spawn.setSpawnTemplate(spawnTemplate);

                    int respawn = 0;
                    int respawnRandom = 0;
                    if (spawnTemplate.getRespawnTime() != null)
                    {
                        respawn = (int) spawnTemplate.getRespawnTime().getSeconds();
                    }
                    if (spawnTemplate.getRespawnTimeRandom() != null)
                    {
                        respawnRandom = (int) spawnTemplate.getRespawnTimeRandom().getSeconds();
                    }

                    if (respawn > 0)
                    {
                        spawn.setRespawnDelay(respawn, respawnRandom);
                        spawn.startRespawn();
                    }
                    else
                    {
                        LOGGER.warning(getClass().getSimpleName() + ": Found database spawns without respawn for npc: " + template.getId() + " - " + template.getName() + " " + spawnTemplate);
                        continue;
                    }

                    addNewSpawn(spawn, rset.getLong("respawnTime"), rset.getDouble("currentHp"), rset.getDouble("currentMp"), false);
                }
                else
                {
                    LOGGER.warning(getClass().getSimpleName() + ": Could not load npc #" + rset.getInt("id") + " from DB");
                }
            }

            LOGGER.info(getClass().getSimpleName() + ": Loaded " + _npcs.size() + " Instances");
            LOGGER.info(getClass().getSimpleName() + ": Scheduled " + _schedules.size() + " Instances");
        }
        catch (SQLException e)
        {
            LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Couldnt load npc_respawns table", e);
        }
        catch (Exception e)
        {
            LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Error while initializing DBSpawnManager: ", e);
        }
    }

    private void scheduleSpawn(int npcId)
    {
        final L2Npc npc = _spawns.get(npcId).doSpawn();
        if (npc != null)
        {
            npc.setDBStatus(DBStatusType.ALIVE);

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
     * @param npc the npc
     * @param isNpcDead the is npc dead
     */
    public void updateStatus(L2Npc npc, boolean isNpcDead)
    {
        final StatsSet info = _storedInfo.get(npc.getId());
        if (info == null)
        {
            return;
        }

        if (isNpcDead)
        {
            npc.setDBStatus(DBStatusType.DEAD);

            final int respawnMinDelay = (int) (npc.getSpawn().getRespawnMinDelay() * Config.RAID_MIN_RESPAWN_MULTIPLIER);
            final int respawnMaxDelay = (int) (npc.getSpawn().getRespawnMaxDelay() * Config.RAID_MAX_RESPAWN_MULTIPLIER);
            final int respawnDelay = Rnd.get(respawnMinDelay, respawnMaxDelay);
            final long respawnTime = System.currentTimeMillis() + respawnDelay;

            info.set("currentHP", npc.getMaxHp());
            info.set("currentMP", npc.getMaxMp());
            info.set("respawnTime", respawnTime);

            if (!_schedules.containsKey(npc.getId()) && ((respawnMinDelay > 0) || (respawnMaxDelay > 0)))
            {
                LOGGER.info(getClass().getSimpleName() + ": Updated " + npc.getName() + " respawn time to " + Util.formatDate(new Date(respawnTime), "dd.MM.yyyy HH:mm"));

                _schedules.put(npc.getId(), ThreadPool.schedule(() -> scheduleSpawn(npc.getId()), respawnDelay));
                updateDb();
            }
        }
        else
        {
            npc.setDBStatus(DBStatusType.ALIVE);

            info.set("currentHP", npc.getCurrentHp());
            info.set("currentMP", npc.getCurrentMp());
            info.set("respawnTime", 0);
        }
        _storedInfo.put(npc.getId(), info);
    }

    /**
     * Adds the new spawn.
     * @param spawn the spawn dat
     * @param respawnTime the respawn time
     * @param currentHP the current hp
     * @param currentMP the current mp
     * @param storeInDb the store in db
     */
    public void addNewSpawn(L2Spawn spawn, long respawnTime, double currentHP, double currentMP, boolean storeInDb)
    {
        if (spawn == null)
        {
            return;
        }
        if (_spawns.containsKey(spawn.getId()))
        {
            return;
        }

        final int npcId = spawn.getId();
        final long time = System.currentTimeMillis();

        SpawnTable.getInstance().addNewSpawn(spawn, false);

        if ((respawnTime == 0) || (time > respawnTime))
        {
            final L2Npc npc = spawn.doSpawn();
            if (npc != null)
            {
                npc.setCurrentHp(currentHP);
                npc.setCurrentMp(currentMP);
                npc.setDBStatus(DBStatusType.ALIVE);

                _npcs.put(npcId, npc);

                final StatsSet info = new StatsSet();
                info.set("currentHP", currentHP);
                info.set("currentMP", currentMP);
                info.set("respawnTime", 0);

                _storedInfo.put(npcId, info);
            }
        }
        else
        {
            final long spawnTime = respawnTime - System.currentTimeMillis();
            _schedules.put(npcId, ThreadPool.schedule(() -> scheduleSpawn(npcId), spawnTime));
        }

        _spawns.put(npcId, spawn);

        if (storeInDb)
        {
            try (Connection con = DatabaseFactory.getConnection();
                 PreparedStatement statement = con.prepareStatement("INSERT INTO npc_respawns (id, x, y, z, heading, respawnTime, currentHp, currentMp) VALUES(?, ?, ?, ?, ?, ?, ?, ?)"))
            {
                statement.setInt(1, spawn.getId());
                statement.setInt(2, spawn.getX());
                statement.setInt(3, spawn.getY());
                statement.setInt(4, spawn.getZ());
                statement.setInt(5, spawn.getHeading());
                statement.setLong(6, respawnTime);
                statement.setDouble(7, currentHP);
                statement.setDouble(8, currentMP);
                statement.execute();
            }
            catch (Exception e)
            {
                // problem with storing spawn
                LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not store npc #" + npcId + " in the DB: ", e);
            }
        }
    }

    public L2Npc addNewSpawn(L2Spawn spawn, boolean storeInDb)
    {
        if (spawn == null)
        {
            return null;
        }

        final int npcId = spawn.getId();
        final L2Spawn existingSpawn = _spawns.get(npcId);
        if (existingSpawn != null)
        {
            return existingSpawn.getLastSpawn();
        }

        SpawnTable.getInstance().addNewSpawn(spawn, false);

        final L2Npc npc = spawn.doSpawn();
        if (npc == null)
        {
            throw new NullPointerException();
        }
        npc.setDBStatus(DBStatusType.ALIVE);

        final StatsSet info = new StatsSet();
        info.set("currentHP", npc.getMaxHp());
        info.set("currentMP", npc.getMaxMp());
        info.set("respawnTime", 0);

        _npcs.put(npcId, npc);
        _storedInfo.put(npcId, info);

        _spawns.put(npcId, spawn);

        if (storeInDb)
        {
            try (Connection con = DatabaseFactory.getConnection();
                 PreparedStatement statement = con.prepareStatement("INSERT INTO npc_respawns (id, x, y, z, heading, respawnTime, currentHp, currentMp) VALUES(?, ?, ?, ?, ?, ?, ?, ?)"))
            {
                statement.setInt(1, spawn.getId());
                statement.setInt(2, spawn.getX());
                statement.setInt(3, spawn.getY());
                statement.setInt(4, spawn.getZ());
                statement.setInt(5, spawn.getHeading());
                statement.setLong(6, 0);
                statement.setDouble(7, npc.getMaxHp());
                statement.setDouble(8, npc.getMaxMp());
                statement.execute();
            }
            catch (Exception e)
            {
                // problem with storing spawn
                LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not store npc #" + npcId + " in the DB: ", e);
            }
        }

        return npc;
    }

    /**
     * Delete spawn.
     * @param spawn the spawn dat
     * @param updateDb the update db
     */
    public void deleteSpawn(L2Spawn spawn, boolean updateDb)
    {
        if (spawn == null)
        {
            return;
        }

        final int npcId = spawn.getId();

        _spawns.remove(npcId);
        _npcs.remove(npcId);
        _storedInfo.remove(npcId);

        final ScheduledFuture<?> task = _schedules.remove(npcId);
        if (task != null)
        {
            task.cancel(true);
        }

        if (updateDb)
        {
            try (Connection con = DatabaseFactory.getConnection();
                 PreparedStatement ps = con.prepareStatement("DELETE FROM npc_respawns WHERE id = ?"))
            {
                ps.setInt(1, npcId);
                ps.execute();
            }
            catch (Exception e)
            {
                // problem with deleting spawn
                LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not remove npc #" + npcId + " from DB: ", e);
            }
        }

        SpawnTable.getInstance().deleteSpawn(spawn, false);
    }

    /**
     * Update database.
     */
    private void updateDb()
    {
        try (Connection con = DatabaseFactory.getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE npc_respawns SET respawnTime = ?, currentHP = ?, currentMP = ? WHERE id = ?"))
        {
            for (Integer npcId : _storedInfo.keySet())
            {
                if (npcId == null)
                {
                    continue;
                }

                final L2Npc npc = _npcs.get(npcId);
                if (npc == null)
                {
                    continue;
                }

                if (npc.getDBStatus() == DBStatusType.ALIVE)
                {
                    updateStatus(npc, false);
                }

                final StatsSet info = _storedInfo.get(npcId);
                if (info == null)
                {
                    continue;
                }

                try
                {
                    statement.setLong(1, info.getLong("respawnTime"));
                    statement.setDouble(2, info.getDouble("currentHP"));
                    statement.setDouble(3, info.getDouble("currentMP"));
                    statement.setInt(4, npcId);
                    statement.executeUpdate();
                    statement.clearParameters();
                }
                catch (SQLException e)
                {
                    LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Couldnt update npc_respawns table ", e);
                }
            }
        }
        catch (SQLException e)
        {
            LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": SQL error while updating database spawn to database: ", e);
        }
    }

    /**
     * Gets the all npc status.
     * @return the all npc status
     */
    public String[] getAllNpcsStatus()
    {
        final String[] msg = new String[(_npcs == null) ? 0 : _npcs.size()];

        if (_npcs == null)
        {
            msg[0] = "None";
            return msg;
        }

        int index = 0;

        for (int i : _npcs.keySet())
        {
            final L2Npc npc = _npcs.get(i);
            msg[index++] = npc.getName() + ": " + npc.getDBStatus().name();
        }

        return msg;
    }

    /**
     * Gets the npc status.
     * @param npcId the npc id
     * @return the raid npc status
     */
    public String getNpcsStatus(int npcId)
    {
        String msg = "NPC Status..." + Config.EOL;

        if (_npcs == null)
        {
            msg += "None";
            return msg;
        }

        if (_npcs.containsKey(npcId))
        {
            final L2Npc npc = _npcs.get(npcId);

            msg += npc.getName() + ": " + npc.getDBStatus().name();
        }

        return msg;
    }

    /**
     * Gets the raid npc status id.
     * @param npcId the npc id
     * @return the raid npc status id
     */
    public DBStatusType getNpcStatusId(int npcId)
    {
        if (_npcs.containsKey(npcId))
        {
            return _npcs.get(npcId).getDBStatus();
        }
        else if (_schedules.containsKey(npcId))
        {
            return DBStatusType.DEAD;
        }
        else
        {
            return DBStatusType.UNDEFINED;
        }
    }

    /**
     * Gets the valid template.
     * @param npcId the npc id
     * @return the valid template
     */
    public L2NpcTemplate getValidTemplate(int npcId)
    {
        return NpcData.getInstance().getTemplate(npcId);
    }

    /**
     * Notify spawn night npc.
     * @param npc the npc
     */
    public void notifySpawnNightNpc(L2Npc npc)
    {
        final StatsSet info = new StatsSet();
        info.set("currentHP", npc.getCurrentHp());
        info.set("currentMP", npc.getCurrentMp());
        info.set("respawnTime", 0);

        npc.setDBStatus(DBStatusType.ALIVE);

        _storedInfo.put(npc.getId(), info);
        _npcs.put(npc.getId(), npc);
    }

    /**
     * Checks if the npc is defined.
     * @param npcId the npc id
     * @return {@code true} if is defined
     */
    public boolean isDefined(int npcId)
    {
        return _spawns.containsKey(npcId);
    }

    /**
     * Gets the npcs.
     * @return the npcs
     */
    public Map<Integer, L2Npc> getNpcs()
    {
        return _npcs;
    }

    /**
     * Gets the spawns.
     * @return the spawns
     */
    public Map<Integer, L2Spawn> getSpawns()
    {
        return _spawns;
    }

    /**
     * Gets the stored info.
     * @return the stored info
     */
    public Map<Integer, StatsSet> getStoredInfo()
    {
        return _storedInfo;
    }

    /**
     * Saves and clears the raid npces status, including all schedules.
     */
    public void cleanUp()
    {
        updateDb();

        _npcs.clear();

        if (_schedules != null)
        {
            for (Integer npcId : _schedules.keySet())
            {
                final ScheduledFuture<?> f = _schedules.get(npcId);
                f.cancel(true);
            }
            _schedules.clear();
        }

        _storedInfo.clear();
        _spawns.clear();
    }

    /**
     * Gets the single instance of DBSpawnManager.
     * @return single instance of DBSpawnManager
     */
    public static DBSpawnManager getInstance()
    {
        return SingletonHolder._instance;
    }

    private static class SingletonHolder
    {
        protected static final DBSpawnManager _instance = new DBSpawnManager();
    }
}
