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
package org.l2j.gameserver.engine.olympiad;

import org.l2j.gameserver.data.database.dao.OlympiadDAO;
import org.l2j.gameserver.data.database.data.OlympiadData;
import org.l2j.gameserver.instancemanager.AntiFeedManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.eventengine.AbstractEvent;
import org.l2j.gameserver.model.eventengine.AbstractEventManager;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadInfo;
import org.l2j.gameserver.util.Broadcast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.function.Consumer;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * @author JoeAlisson
 */
public class OlympiadEngine extends AbstractEventManager<AbstractEvent<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OlympiadEngine.class);

    private OlympiadData data = new OlympiadData();
    private boolean matchInProgress;
    private ConsumerEventListener onPlayerLoginListener;

    private OlympiadEngine() {
    }

    @Override
    public void onInitialized() {
        data = getDAO(OlympiadDAO.class).findData();

        var startDate = LocalDate.parse(getVariables().getString("start-date", "2020-06-22"));
        if(isNull(data)) {
            data = new OlympiadData();
            data.setNextSeasonDate(startDate);
            getDAO(OlympiadDAO.class).save(data);
        } else if(getVariables().getBoolean("force-start-date", false)) {
            data.setNextSeasonDate(startDate);
        }

        if(data.getNextSeasonDate().isAfter(LocalDate.now())) {
            LOGGER.info("World Olympiad season start scheduled to {}", data.getNextSeasonDate());
        } else if(data.getSeason() > 0) {
            LOGGER.info("World Olympiad {} season has been started", data.getSeason());
        }

        AntiFeedManager.getInstance().registerEvent(AntiFeedManager.OLYMPIAD_ID);
    }

    public void onStartMatch() {
        matchInProgress = true;
        Broadcast.toAllOnlinePlayers(ExOlympiadInfo.show(OlympiadRuleType.MAX, 300));
        var listeners = Listeners.players();
        onPlayerLoginListener = new ConsumerEventListener(listeners, EventType.ON_PLAYER_LOGIN, (Consumer<OnPlayerLogin>) e -> onPlayerLogin(e.getPlayer()), this);
        listeners.addListener(onPlayerLoginListener);
        Broadcast.toAllOnlinePlayers(getSystemMessage(SystemMessageId.SHARPEN_YOUR_SWORDS_TIGHTEN_THE_STITCHING_IN_YOUR_ARMOR_AND_MAKE_HASTE_TO_A_OLYMPIAD_MANAGER_BATTLES_IN_THE_OLYMPIAD_GAMES_ARE_NOW_TAKING_PLACE));
    }

    private void onPlayerLogin(Player player) {
        if(matchInProgress) {
            player.sendPacket(ExOlympiadInfo.show(OlympiadRuleType.MAX, 300));
        }
    }

    public void onStopMatch() {
        matchInProgress = false;
        if(nonNull(onPlayerLoginListener)) {
            Listeners.players().removeListener(onPlayerLoginListener);
        }
        Broadcast.toAllOnlinePlayers(ExOlympiadInfo.hide(OlympiadRuleType.MAX));
        Broadcast.toAllOnlinePlayers(getSystemMessage(SystemMessageId.THE_OLYMPIAD_REGISTRATION_PERIOD_HAS_ENDED));
    }

    public void onNewSeason(){
        if(LocalDate.now().compareTo(data.getNextSeasonDate()) >= 0) {
            data.increaseSeason();
            getDAO(OlympiadDAO.class).save(data);
            Broadcast.toAllOnlinePlayers(getSystemMessage(SystemMessageId.ROUND_S1_OF_THE_OLYMPIAD_GAMES_HAS_STARTED).addInt(data.getSeason()));
        }
    }

    public boolean isMatchInProgress() {
        return matchInProgress;
    }

    public int getCurrentSeason() {
        return data.getSeason();
    }

    public int getPeriod() {
        return data.getPeriod();
    }

    public void saveOlympiadStatus() {
        getDAO(OlympiadDAO.class).save(data);
    }

    public int getOlympiadPoints(Player player) {
        return 0;
    }

    public int getRemainingDailyMatches(Player player) {
        return 5;
    }

    public static OlympiadEngine getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final OlympiadEngine INSTANCE = new OlympiadEngine();
    }

}
