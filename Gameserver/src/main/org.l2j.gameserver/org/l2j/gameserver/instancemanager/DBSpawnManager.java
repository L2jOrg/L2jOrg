/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.instancemanager;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
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
import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.zeroIfNullOrElse;

/**
 * Database spawn manager.
 *
 * @author godson, UnAfraid
 * @author JoeAlisson
 */
public class DBSpawnManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBSpawnManager.class);

    private final IntMap<Npc> npcs = new CHashIntMap<>();
    private final IntMap<Spawn> spawns = new CHashIntMap<>();
    private final IntMap<StatsSet> storedInfo = new CHashIntMap<>();
    private final IntMap<ScheduledFuture<?>> schedules = new CHashIntMap<>();

    private DBSpawnManager() {
    }

    private void load() {
        if (Config.ALT_DEV_NO_SPAWNS) {
            return;
        }

        npcs.clear();
        spawns.clear();
        storedInfo.clear();
        schedules.clear();

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT * FROM npc_respawns");
             ResultSet rset = statement.executeQuery()) {

            while (rset.next()) {
                final NpcTemplate template = getValidTemplate(rset.getInt("id"));

                if (nonNull(template)) {
                    final Spawn spawn = new Spawn(template);
                    spawn.setXYZ(rset.getInt("x"), rset.getInt("y"), rset.getInt("z"));
                    spawn.setAmount(1);
                    spawn.setHeading(rset.getInt("heading"));

                    final List<NpcSpawnTemplate> spawns = SpawnsData.getInstance().getNpcSpawns(npc -> npc.getId() == template.getId() && npc.hasDBSave());

                    if (spawns.isEmpty()) {
                        LOGGER.warn("Couldn't find spawn declaration for npc: {} - {} ", template.getId(), template.getName());
                        deleteSpawn(spawn, true);
                        continue;
                    } else if (spawns.size() > 1) {
                        LOGGER.warn("Found multiple database spawns for npc: {} - {} {}", template.getId(),  template.getName(), spawns);
                        continue;
                    }

                    final NpcSpawnTemplate spawnTemplate = spawns.get(0);
                    spawn.setSpawnTemplate(spawnTemplate);

                    int respawn = zeroIfNullOrElse(spawnTemplate.getRespawnTime(), d -> (int) d.getSeconds());
                    int respawnRandom = zeroIfNullOrElse(spawnTemplate.getRespawnTimeRandom(), d -> (int) d.getSeconds());

                    if (respawn > 0) {
                        spawn.setRespawnDelay(respawn, respawnRandom);
                        spawn.startRespawn();
                    } else {
                        spawn.stopRespawn();
                        LOGGER.warn("Found database spawns without respawn for npc: {} - {} {}", template.getId(), template.getName(), spawnTemplate);
                        continue;
                    }

                    addNewSpawn(spawn, rset.getLong("respawnTime"), rset.getDouble("currentHp"), rset.getDouble("currentMp"), false);
                } else {
                    LOGGER.warn("Could not load npc id {} from DB from DB", rset.getInt("id"));
                }
            }

            LOGGER.info("Loaded {} Instances", npcs.size());
            LOGGER.info("Scheduled {} Instances", schedules.size());
        } catch (SQLException e) {
            LOGGER.warn("Couldn't load npc_respawns table", e);
        } catch (Exception e) {
            LOGGER.warn("Error while initializing DBSpawnManager: ", e);
        }
    }

    private void scheduleSpawn(int npcId) {
        final Npc npc = spawns.get(npcId).doSpawn();
        if (npc != null) {
            npc.setRaidBossStatus(RaidBossStatus.ALIVE);

            final StatsSet info = new StatsSet();
            info.set("currentHP", npc.getCurrentHp());
            info.set("currentMP", npc.getCurrentMp());
            info.set("respawnTime", 0);

            storedInfo.put(npcId, info);
            npcs.put(npcId, npc);
            LOGGER.info("Spawning NPC {}", npc.getName());
        }

        schedules.remove(npcId);
    }

    /**
     * Update status.
     *
     * @param npc       the npc
     * @param isNpcDead the is npc dead
     */
    public void updateStatus(Npc npc, boolean isNpcDead) {
        final StatsSet info = storedInfo.get(npc.getId());
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

            if (!schedules.containsKey(npc.getId()) && ((respawnMinDelay > 0) || (respawnMaxDelay > 0))) {
                LOGGER.info(getClass().getSimpleName() + ": Updated " + npc.getName() + " respawn time to " + GameUtils.formatDate(new Date(respawnTime), "dd.MM.yyyy HH:mm"));

                schedules.put(npc.getId(), ThreadPool.schedule(() -> scheduleSpawn(npc.getId()), respawnDelay));
                updateDb();
            }
        } else {
            npc.setRaidBossStatus(RaidBossStatus.ALIVE);

            info.set("currentHP", npc.getCurrentHp());
            info.set("currentMP", npc.getCurrentMp());
            info.set("respawnTime", 0);
        }
        storedInfo.put(npc.getId(), info);
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
        if (isNull(spawn)) {
            return;
        }
        if (spawns.containsKey(spawn.getId())) {
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

                npcs.put(npcId, npc);

                final StatsSet info = new StatsSet();
                info.set("currentHP", currentHP);
                info.set("currentMP", currentMP);
                info.set("respawnTime", 0);

                storedInfo.put(npcId, info);
            }
        } else {
            final long spawnTime = respawnTime - System.currentTimeMillis();
            schedules.put(npcId, ThreadPool.schedule(() -> scheduleSpawn(npcId), spawnTime));
        }

        spawns.put(npcId, spawn);

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
        final Spawn existingSpawn = spawns.get(npcId);
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

        npcs.put(npcId, npc);
        storedInfo.put(npcId, info);

        spawns.put(npcId, spawn);

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

        spawns.remove(npcId);
        npcs.remove(npcId);
        storedInfo.remove(npcId);

        final ScheduledFuture<?> task = schedules.remove(npcId);
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

            storedInfo.keySet().forEach(npcId -> {
                final Npc npc = npcs.get(npcId);
                if (npc == null) {
                    return;
                }

                if (npc.getRaidBossStatus() == RaidBossStatus.ALIVE) {
                    updateStatus(npc, false);
                }

                final StatsSet info = storedInfo.get(npcId);
                if (info == null) {
                    return;
                }

                try {
                    statement.setLong(1, info.getLong("respawnTime"));
                    statement.setDouble(2, npc.isDead() ? npc.getMaxHp() : info.getDouble("currentHP"));
                    statement.setDouble(3, npc.isDead() ? npc.getMaxMp() : info.getDouble("currentMP"));
                    statement.setInt(4, npcId);
                    statement.executeUpdate();
                    statement.clearParameters();
                } catch (SQLException e) {
                    LOGGER.warn("Couldnt update npc_respawns table ", e);
                }
            });
        } catch (SQLException e) {
            LOGGER.warn("SQL error while updating database spawn to database: ", e);
        }
    }

    /**
     * Gets the raid npc status id.
     *
     * @param npcId the npc id
     * @return the raid npc status id
     */
    public RaidBossStatus getNpcStatusId(int npcId) {
        if (npcs.containsKey(npcId)) {
            return npcs.get(npcId).getRaidBossStatus();
        } else if (schedules.containsKey(npcId)) {
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

        storedInfo.put(npc.getId(), info);
        npcs.put(npc.getId(), npc);
    }

    /**
     * Checks if the npc is defined.
     *
     * @param npcId the npc id
     * @return {@code true} if is defined
     */
    public boolean isDefined(int npcId) {
        return spawns.containsKey(npcId);
    }

    public IntMap<Npc> getNpcs() {
        return npcs;
    }

    public IntMap<Spawn> getSpawns() {
        return spawns;
    }

    /**
     * Saves and clears the raid npces status, including all schedules.
     */
    public void cleanUp() {
        updateDb();
        npcs.clear();
        schedules.values().forEach(s -> s.cancel(true));
        schedules.clear();
        storedInfo.clear();
        spawns.clear();
    }

    public static void init() {
        getInstance().load();
    }

    public static DBSpawnManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final DBSpawnManager INSTANCE = new DBSpawnManager();
    }
}
