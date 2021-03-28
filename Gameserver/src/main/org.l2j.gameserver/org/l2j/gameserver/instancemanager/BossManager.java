/*
 * Copyright © 2019-2021 L2JOrg
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
import io.github.joealisson.primitive.Containers;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.dao.BossDAO;
import org.l2j.gameserver.data.database.data.BossData;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.data.xml.impl.SpawnsData;
import org.l2j.gameserver.datatables.SpawnTable;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.spawns.NpcSpawnTemplate;
import org.l2j.gameserver.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 *
 * @author godson, UnAfraid
 * @author JoeAlisson
 */
public class BossManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(BossManager.class);

    private final IntMap<Npc> npcs = new CHashIntMap<>();
    private final IntMap<Spawn> spawns = new CHashIntMap<>();
    private final IntMap<ScheduledFuture<?>> schedules = new CHashIntMap<>();
    private IntMap<BossData> bossesData = Containers.emptyIntMap();

    private BossManager() {
    }

    private void load() {
        if (Config.ALT_DEV_NO_SPAWNS) {
            return;
        }

        npcs.clear();
        spawns.clear();
        schedules.clear();

        bossesData = getDAO(BossDAO.class).findAllBosses();
        for (BossData bossData : bossesData.values()) {
            addSpawn(bossData);
        }

        LOGGER.info("Loaded {} Instances", npcs.size());
        LOGGER.info("Scheduled {} Instances", schedules.size());
    }

    private void addSpawn(BossData bossData) {
        final NpcTemplate template = NpcData.getInstance().getTemplate(bossData.getBossId());

        if (nonNull(template)) {
            final Spawn spawn = createSpawn(bossData, template);
            if (isNull(spawn)) {
                return;
            }

            addNewSpawn(spawn, bossData.getRespawnTime(), bossData.getHp(), bossData.getMp(), false);
        } else {
            LOGGER.warn("Could not load npc id {} from DB from DB", bossData.getBossId());
        }
    }

    private Spawn createSpawn(BossData bossData, NpcTemplate template) {
        final var spawnTemplate = findNpcSpawnTemplate(template);

        if(isNull(spawnTemplate)) {
            return null;
        }

        int respawn = (int) Util.zeroIfNullOrElseLong(spawnTemplate.getRespawnTime(), Duration::getSeconds);
        if(respawn <= 0) {
            LOGGER.warn("Found database spawns without respawn for npc: {} - {} {}", template.getId(), template.getName(), spawnTemplate);
            return null;
        }

        return initSpawn(bossData, template, spawnTemplate, respawn);
    }

    private Spawn initSpawn(BossData bossData, NpcTemplate template, NpcSpawnTemplate spawnTemplate, int respawn) {
        final Spawn spawn;
        try {
            spawn = new Spawn(template);
            spawn.setXYZ(bossData.getX(), bossData.getY(), bossData.getZ());
            spawn.setAmount(1);
            spawn.setHeading(bossData.getHeading());
            spawn.setSpawnTemplate(spawnTemplate);

            int respawnRandom = (int) Util.zeroIfNullOrElseLong(spawnTemplate.getRespawnTimeRandom(), Duration::getSeconds);
            spawn.setRespawnDelay(respawn, respawnRandom);
            spawn.startRespawn();

        } catch (NoSuchMethodException | ClassNotFoundException e) {
            LOGGER.error("Couldn't create boss spawn to template {} ", template, e);
            return null;
        }
        return spawn;
    }

    private NpcSpawnTemplate findNpcSpawnTemplate(NpcTemplate template) {
        NpcSpawnTemplate spawnTemplate = null;
        List<NpcSpawnTemplate> spawns = SpawnsData.getInstance().getNpcSpawns(npc -> npc.getId() == template.getId() && npc.hasDBSave());

        if (spawns.isEmpty()) {
            LOGGER.warn("Couldn't find spawn declaration for npc: {} - {} ", template.getId(), template.getName());
            getDAO(BossDAO.class).deleteBossData(template.getId());
        } else if (spawns.size() > 1) {
            LOGGER.warn("Found multiple database spawns for npc: {} - {} {}", template.getId(),  template.getName(), spawns);
        } else {
            spawnTemplate = spawns.get(0);
        }
        return spawnTemplate;
    }

    private void scheduleSpawn(int npcId) {
        final Npc npc = spawns.get(npcId).doSpawn();
        if (nonNull(npc)) {
            npc.setRaidBossStatus(BossStatus.ALIVE);
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
        final var data = bossesData.get(npc.getId());
        if (data == null) {
            return;
        }

        if (isNpcDead) {
            npc.setRaidBossStatus(BossStatus.DEAD);

            final int respawnMinDelay = (int) (npc.getSpawn().getRespawnMinDelay() * Config.RAID_MIN_RESPAWN_MULTIPLIER);
            final int respawnMaxDelay = (int) (npc.getSpawn().getRespawnMaxDelay() * Config.RAID_MAX_RESPAWN_MULTIPLIER);
            final int respawnDelay = Rnd.get(respawnMinDelay, respawnMaxDelay);
            final long respawnTime = System.currentTimeMillis() + respawnDelay;

            data.setHp(npc.getMaxHp());
            data.setMp(npc.getMaxMp());
            data.setRespawnTime(respawnTime);

            if (!schedules.containsKey(npc.getId()) && ((respawnMinDelay > 0) || (respawnMaxDelay > 0))) {
                LOGGER.info("Updated {} respawn time to {}", npc.getName(), GameUtils.formatDate(new Date(respawnTime), "dd.MM.yyyy HH:mm"));
                schedules.put(npc.getId(), ThreadPool.schedule(() -> scheduleSpawn(npc.getId()), respawnDelay));
            }
        } else {
            npc.setRaidBossStatus(BossStatus.ALIVE);

            data.setHp(npc.getCurrentHp());
            data.setMp(npc.getCurrentMp());
            data.setRespawnTime(0);
        }
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
        if (isNull(spawn)  || spawns.containsKey(spawn.getId())) {
            return;
        }

        SpawnTable.getInstance().addNewSpawn(spawn, false);

        final int npcId = spawn.getId();
        var data = bossesData.computeIfAbsent(npcId, id ->  BossData.of(id, spawn));
        data.setRespawnTime(0);
        data.setHp(currentHP);
        data.setMp(currentMP);

        final long time = System.currentTimeMillis();
        final long spawnTime = respawnTime - time;

        if (respawnTime == 0 || spawnTime < 20000) {
            final Npc npc = spawn.doSpawn();
            if (nonNull(npc)) {
                npc.setCurrentHp(currentHP);
                npc.setCurrentMp(currentMP);
                npc.setRaidBossStatus(BossStatus.ALIVE);
                npcs.put(npcId, npc);
            }
        } else {
            schedules.put(npcId, ThreadPool.schedule(() -> scheduleSpawn(npcId), spawnTime));
        }

        spawns.put(npcId, spawn);
        if (storeInDb) {
            getDAO(BossDAO.class).save(data);
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
        npc.setRaidBossStatus(BossStatus.ALIVE);

        var data = bossesData.computeIfAbsent(npcId, id ->  BossData.of(id, spawn));
        data.setRespawnTime(0);
        data.setHp(npc.getMaxHp());
        data.setMp(npc.getMaxMp());

        npcs.put(npcId, npc);
        spawns.put(npcId, spawn);

        if (storeInDb) {
           getDAO(BossDAO.class).save(data);
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
        bossesData.remove(npcId);

        final ScheduledFuture<?> task = schedules.remove(npcId);
        if (task != null) {
            task.cancel(true);
        }

        if (updateDb) {
            getDAO(BossDAO.class).deleteBossData(npcId);
        }

        SpawnTable.getInstance().deleteSpawn(spawn, false);
    }

    /**
     * Gets the raid npc status id.
     *
     * @param npcId the npc id
     * @return the raid npc status id
     */
    public BossStatus getNpcStatusId(int npcId) {
        if (npcs.containsKey(npcId)) {
            return npcs.get(npcId).getRaidBossStatus();
        } else if (schedules.containsKey(npcId)) {
            return BossStatus.DEAD;
        } else {
            return null;
        }
    }

    /**
     * Notify spawn night npc.
     *
     * @param npc the npc
     */
    public void notifySpawnNightNpc(Npc npc) {
        var data = bossesData.computeIfAbsent(npc.getId(), id ->  BossData.of(id, npc.getSpawn()));
        data.setRespawnTime(0);
        data.setHp(npc.getCurrentHp());
        data.setMp(npc.getCurrentMp());

        npc.setRaidBossStatus(BossStatus.ALIVE);
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

    /**
     * Saves and clears the raid npces status, including all schedules.
     */
    public void cleanUp() {
        getDAO(BossDAO.class).save(bossesData.values());
        npcs.clear();
        schedules.values().forEach(s -> s.cancel(true));
        schedules.clear();
        bossesData.clear();
        spawns.clear();
    }

    public static void init() {
        getInstance().load();
    }

    public static BossManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final BossManager INSTANCE = new BossManager();
    }
}
