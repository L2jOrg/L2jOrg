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
package org.l2j.gameserver.engine.timedzone;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.data.database.data.TimeRestrictZoneInfo;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogout;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.network.serverpackets.timedzone.TimeRestrictFieldList;
import org.l2j.gameserver.world.zone.ZoneEngine;
import org.l2j.gameserver.world.zone.type.TimeRestrictZone;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author JoeAlisson
 */
public class TimeRestrictZoneEngine {

    private final IntMap<IntMap<TimeRestrictZoneInfo>> timedRestrictZoneInfos = new CHashIntMap<>();
    private final Object TASK_LOCKER = new Object();

    private ScheduledFuture<?> taskTimeCheck;

    private TimeRestrictZoneEngine() {
        var listener = Listeners.players();
        listener.addListener(new ConsumerEventListener(listener, EventType.ON_PLAYER_LOGOUT, (Consumer<OnPlayerLogout>) this::onPlayerLogout, this));
    }

    private void onPlayerLogout(OnPlayerLogout event) {
        var player = event.getPlayer();
        var infos = timedRestrictZoneInfos.remove(player.getObjectId());
        getDAO(PlayerDAO.class).saveRestrictZoneInfo(infos.values());
    }

    public TimeRestrictZoneInfo getTimeRestrictZoneInfo(Player player, TimeRestrictZone zone) {
        return timedRestrictZoneInfos.computeIfAbsent(player.getObjectId(), this::loadPlayerTimeRestrictZoneInfo).computeIfAbsent(zone.getId(), zoneId -> TimeRestrictZoneInfo.init(zone, player.getObjectId()));
    }

    private IntMap<TimeRestrictZoneInfo> loadPlayerTimeRestrictZoneInfo(int playerId) {
        return getDAO(PlayerDAO.class).loadTimeRestrictZoneInfo(playerId);
    }

    public void startRemainingTimeCheck() {
        synchronized (TASK_LOCKER) {
            if(taskTimeCheck == null) {
                taskTimeCheck = ThreadPool.scheduleAtFixedDelay(new TimedZoneTask(), 1, 1, TimeUnit.MINUTES);
            }
        }
    }

    public void showZones(Player player) {
        timedRestrictZoneInfos.computeIfAbsent(player.getObjectId(), this::loadPlayerTimeRestrictZoneInfo);
        player.sendPacket(new TimeRestrictFieldList());
    }

    public class TimedZoneTask implements Runnable {

        @Override
        public void run() {
            var zones = ZoneEngine.getInstance().getAllZones(TimeRestrictZone.class);
            var updated = new AtomicBoolean(false);
            for (var zone : zones) {
                zone.forEachPlayer(player -> {
                    updated.set(true);
                    var info = getTimeRestrictZoneInfo(player, zone);
                    info.updateRemainingTime();
                    if(info.remainingTime() <= 0) {
                        player.teleToLocation(TeleportWhereType.TOWN);
                    }
                });
            }

            if(!updated.get()) {
                synchronized (TASK_LOCKER) {
                    if(taskTimeCheck != null) {
                        taskTimeCheck.cancel(false);
                        taskTimeCheck = null;
                    }
                }
            }
        }
    }

    public static TimeRestrictZoneEngine getInstance() {
        return Singleton.INSTANCE;
    }

    private static final class Singleton {
        private static final TimeRestrictZoneEngine INSTANCE = new TimeRestrictZoneEngine();
    }
}
