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
package org.l2j.gameserver.engine.siege;

import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.eventengine.AbstractEventManager;
import org.l2j.gameserver.model.eventengine.ScheduleTarget;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.network.serverpackets.siege.ExMercenarySiegeHUDInfo;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public class SiegeEngine extends AbstractEventManager<Siege> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SiegeEngine.class);

    private SiegeState state;
    private SiegeSettings settings;
    private ConsumerEventListener loginListener;
    private List<Siege> sieges = Collections.emptyList();

    private SiegeEngine() {

    }

    @Override
    public void onInitialized() {
        state = SiegeState.NONE;
    }

    @Override
    public void config(GameXmlReader reader, Node configNode) {
        settings = SiegeSettings.parse(reader, configNode);
    }

    @ScheduleTarget
    public void onPreparationStart() {
        filterScheduledSieges();
        if(!sieges.isEmpty()) {
            state = SiegeState.PREPARATION;
            loginListener = new ConsumerEventListener(null, EventType.ON_PLAYER_LOGIN, (Consumer<OnPlayerLogin>) this::onPlayerLogin, this);
            Listeners.players().addListener(loginListener);

            for (Siege siege : sieges) {
                Broadcast.toAllOnlinePlayers(new ExMercenarySiegeHUDInfo(siege.getCastle()));
            }
        } else {
            LOGGER.info("There is no siege scheduled at this time");
        }
    }

    private void filterScheduledSieges() {
        var now = LocalDateTime.now();
        var siegeRemainingTime = getScheduler("stop-siege").getRemainingTime(TimeUnit.MINUTES);
        var endHour = now.plusMinutes(siegeRemainingTime).getHour();

        sieges = new ArrayList<>();

        for (var schedulesEntry : settings.siegeSchedules.entrySet()) {
            var castleId = schedulesEntry.getKey();
            var castle = CastleManager.getInstance().getCastleById(castleId);

            if(nonNull(castle) && castle.getSiegeDate().withHour(0).compareTo(now) <= 0) {
                var schedules = schedulesEntry.getValue();
                for (SiegeSchedule schedule : schedules) {
                    if(schedule.day() == now.getDayOfWeek() && schedule.hour() >= now.getHour() && schedule.hour() <= endHour) {
                        sieges.add(new Siege(castle));
                        break;
                    }
                }
            }
        }
    }

    private void onPlayerLogin(OnPlayerLogin event) {
        final var player = event.getPlayer();
        for (Siege siege : sieges) {
            player.sendPacket(new ExMercenarySiegeHUDInfo(siege.getCastle()));
        }
    }

    @ScheduleTarget
    public void onSiegeStart() {
        state = SiegeState.STARTED;

    }

    @ScheduleTarget
    public void onSiegeStop() {
        Listeners.players().removeListener(loginListener);
        loginListener = null;
        state = SiegeState.NONE;
    }

    public static SiegeEngine getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final SiegeEngine INSTANCE = new SiegeEngine();
    }
}
