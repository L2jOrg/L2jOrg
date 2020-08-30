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

import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.data.database.dao.OlympiadDAO;
import org.l2j.gameserver.data.database.data.OlympiadData;
import org.l2j.gameserver.instancemanager.AntiFeedManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.eventengine.AbstractEvent;
import org.l2j.gameserver.model.eventengine.AbstractEventManager;
import org.l2j.gameserver.model.eventengine.ScheduleTarget;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadInfo;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadMatchMakingResult;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.gameserver.engine.olympiad.OlympiadState.*;
import static org.l2j.gameserver.network.SystemMessageId.*;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * @author JoeAlisson
 */
public class Olympiad extends AbstractEventManager<OlympiadMatch> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Olympiad.class);

    private final Set<Player> matchMaking = ConcurrentHashMap.newKeySet(20);
    private final Set<OlympiadMatch> matches = ConcurrentHashMap.newKeySet(10);
    private ConsumerEventListener onPlayerLoginListener;
    private OlympiadData data = new OlympiadData();
    private OlympiadState state = OlympiadState.SCHEDULED;
    private LocalDate startDate;
    private boolean forceStartDate;
    private int minParticipant;

    private Olympiad() {
    }

    @Override
    public void config(GameXmlReader reader, Node configNode) {
        final var olympiadConfig= configNode.getFirstChild();
        if(nonNull(olympiadConfig) && olympiadConfig.getNodeName().equals("olympiad-config")) {
            final var attr = olympiadConfig.getAttributes();
            minParticipant = reader.parseInt(attr, "min-participant");
            forceStartDate = reader.parseBoolean(attr, "force-start-date");
            String strDate = reader.parseString(attr, "start-date");
            startDate = isNull(strDate) ? LocalDate.now() : LocalDate.parse(strDate);
        }
    }

    @Override
    public void onInitialized() {
        data = getDAO(OlympiadDAO.class).findData();

        if(isNull(data)) {
            data = new OlympiadData();
            data.setNextSeasonDate(startDate);
            data.setId(1);
            getDAO(OlympiadDAO.class).save(data);
        } else if(forceStartDate) {
            data.setNextSeasonDate(startDate);
        }

        if(data.getNextSeasonDate().isAfter(LocalDate.now())) {
            LOGGER.info("World Olympiad season start scheduled to {}", data.getNextSeasonDate());
        } else if(data.getSeason() > 0) {
            LOGGER.info("World Olympiad {} season has been started", data.getSeason());
        }

        AntiFeedManager.getInstance().registerEvent(AntiFeedManager.OLYMPIAD_ID);
    }

    @ScheduleTarget
    public void onStartMatch() {
        state = STARTED;
        Broadcast.toAllOnlinePlayers(ExOlympiadInfo.show(OlympiadRuleType.CLASSLESS, 300));
        var listeners = Listeners.players();
        onPlayerLoginListener = new ConsumerEventListener(listeners, EventType.ON_PLAYER_LOGIN, (Consumer<OnPlayerLogin>) e -> onPlayerLogin(e.getPlayer()), this);
        listeners.addListener(onPlayerLoginListener);
        Broadcast.toAllOnlinePlayers(getSystemMessage(SystemMessageId.SHARPEN_YOUR_SWORDS_TIGHTEN_THE_STITCHING_IN_YOUR_ARMOR_AND_MAKE_HASTE_TO_A_OLYMPIAD_MANAGER_BATTLES_IN_THE_OLYMPIAD_GAMES_ARE_NOW_TAKING_PLACE));
    }

    private void onPlayerLogin(Player player) {
        if(state.matchesInProgress()) {
            var scheduler = getScheduler("stop-match");
            if(nonNull(scheduler)) {
                player.sendPacket(ExOlympiadInfo.show(OlympiadRuleType.CLASSLESS, (int) scheduler.getRemainingTime(TimeUnit.SECONDS)));
            } else {
                LOGGER.warn("Can't find stop-match scheduler");
            }
        }
    }

    @ScheduleTarget
    public void onStopMatch() {
        state = SCHEDULED;
        if(nonNull(onPlayerLoginListener)) {
            Listeners.players().removeListener(onPlayerLoginListener);
        }
        Broadcast.toAllOnlinePlayers(ExOlympiadInfo.hide(OlympiadRuleType.CLASSLESS));
        Broadcast.toAllOnlinePlayers(getSystemMessage(SystemMessageId.THE_OLYMPIAD_REGISTRATION_PERIOD_HAS_ENDED));
    }

    @ScheduleTarget
    public void onNewSeason(){
        if(LocalDate.now().compareTo(data.getNextSeasonDate()) >= 0) {
            data.increaseSeason();
            getDAO(OlympiadDAO.class).save(data);
            Broadcast.toAllOnlinePlayers(getSystemMessage(SystemMessageId.ROUND_S1_OF_THE_OLYMPIAD_GAMES_HAS_STARTED).addInt(data.getSeason()));
        }
    }

    public boolean isMatchesInProgress() {
        return state.matchesInProgress();
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

    public void registerPlayer(Player player, OlympiadRuleType ruleType) {
        if(!state.matchesInProgress())  {
            return;
        }

        if(matchMaking.contains(player)) {
            player.sendPacket(getSystemMessage(C1_YOU_HAVE_ALREADY_REGISTERED_FOR_THE_MATCH).addPcName(player));
            return;
        }

        matchMaking.add(player);
        player.sendPacket(YOU_VE_BEEN_REGISTERED_IN_THE_WAITING_LIST_OF_ALL_CLASS_BATTLE);
        player.sendPacket(new ExOlympiadMatchMakingResult(true, ruleType));

        if(state != MATCH_MAKING && matchMaking.size() >= minParticipant) {
            state = MATCH_MAKING;
            ThreadPool.schedule(this::distributeAndStartMatches, 1, TimeUnit.MINUTES);
        }
    }

    private void distributeAndStartMatches() {
        List<Player> participants;
        synchronized (matchMaking) {
            participants = new ArrayList<>(matchMaking);
            matchMaking.clear();
        }

        while(participants.size() >= OlympiadRuleType.CLASSLESS.participantCount()) {
            newMatch(participants);
        }

        if(!participants.isEmpty()) {
            matchMaking.addAll(participants);
        }
        if(state == MATCH_MAKING) {
            state = STARTED;
        }
    }

    private void newMatch(List<Player> participants) {
        OlympiadMatch match = OlympiadMatch.of(OlympiadRuleType.CLASSLESS);

        for (int i = 0; i < OlympiadRuleType.CLASSLESS.participantCount(); i++) {
            match.addParticipant(participants.remove(Rnd.get(participants.size())));
        }

        matches.add(match);
        ThreadPool.schedule(match, 1, TimeUnit.MINUTES);
    }

    public void unregisterPlayer(Player player, OlympiadRuleType ruleType) {
        if(matchMaking.remove(player)) {
            player.sendPacket(YOU_HAVE_BEEN_REMOVED_FROM_THE_OLYMPIAD_WAITING_LIST);
            player.sendPacket(new ExOlympiadMatchMakingResult(false, ruleType));
        }
    }

    public boolean isRegistered(Player player) {
        return matchMaking.contains(player);
    }

    @Override
    protected void onPlayerLogout(Player player) {
        unregisterPlayer(player, OlympiadRuleType.CLASSLESS);
    }

    public int getSeasonMonth() {
        return YearMonth.now().getMonthValue();
    }

    public int getSeasonYear() {
        return Year.now().getValue();
    }

    public static Olympiad getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final Olympiad INSTANCE = new Olympiad();
    }

}
