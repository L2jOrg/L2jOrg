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
import org.l2j.gameserver.data.database.dao.GrandBossDAO;
import org.l2j.gameserver.data.database.data.GrandBossData;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.instancemanager.tasks.GrandBossManagerStoreTask;
import org.l2j.gameserver.model.actor.instance.GrandBoss;
import org.l2j.gameserver.model.interfaces.IStorable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.computeIfNonNull;

/**
 * Grand Boss manager.
 *
 * @author DaRkRaGe Revised by Emperorc
 * @author JoeAlisson
 */
public final class GrandBossManager implements IStorable {

    protected static Logger LOGGER = LoggerFactory.getLogger(GrandBossManager.class.getName());

    protected static IntMap<GrandBoss> bosses = new CHashIntMap<>();
    private IntMap<GrandBossData> grandBossesData = Containers.emptyIntMap();

    private GrandBossManager() {
        init();
    }

    private void init() {
        grandBossesData = getDAO(GrandBossDAO.class).loadGrandBosses();
        LOGGER.info("Loaded {} Grand Bosses.", grandBossesData.size());
        ThreadPool.scheduleAtFixedRate(new GrandBossManagerStoreTask(), 5 * 60 * 1000, 5 * 60 * 1000);
    }

    public BossStatus getBossStatus(int bossId) {
        return computeIfNonNull(grandBossesData.get(bossId), GrandBossData::getStatus);
    }

    public void setBossStatus(int bossId, BossStatus status) {
        var bossData = grandBossesData.get(bossId);
        if(nonNull(bossData)) {
            bossData.setStatus(status);
            getDAO(GrandBossDAO.class).updateGrandBossStatus(bossId, status);
            LOGGER.info("Updated {} ({}) status to {}", NpcData.getInstance().getTemplate(bossId).getName(), bossId, status );
        }
    }

    public void addBoss(GrandBoss boss) {
        if (nonNull(boss)) {
            bosses.put(boss.getId(), boss);
        }
    }

    public boolean isDefined(int bossId) {
        return grandBossesData.containsKey(bossId);
    }

    public GrandBossData getBossData(int bossId) {
        return grandBossesData.get(bossId);
    }

    @Override
    public boolean storeMe() {
        for (IntMap.Entry<GrandBoss> entry : bosses.entrySet()) {
            var boss= entry.getValue();
            var data = grandBossesData.get(entry.getKey());
            data.setX(boss.getX());
            data.setY(boss.getY());
            data.setZ(boss.getZ());
            data.setHeading(boss.getHeading());
            if(boss.isDead()) {
                data.setHp(boss.getMaxHp());
                data.setMp(boss.getMaxMp());
            } else {
                data.setHp(boss.getCurrentHp());
                data.setMp(boss.getCurrentMp());
            }
        }

        return getDAO(GrandBossDAO.class).save(grandBossesData.values());
    }

    /**
     * Saves all Grand Boss info and then clears all info from memory, including all schedules.
     */
    public void cleanUp() {
        storeMe();
        bosses.clear();
    }

    public static GrandBossManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final GrandBossManager INSTANCE = new GrandBossManager();
    }
}
