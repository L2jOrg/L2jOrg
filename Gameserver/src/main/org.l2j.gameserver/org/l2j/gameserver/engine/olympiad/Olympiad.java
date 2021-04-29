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
package org.l2j.gameserver.engine.olympiad;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.commons.util.collection.LimitedQueue;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.data.database.dao.OlympiadDAO;
import org.l2j.gameserver.data.database.data.*;
import org.l2j.gameserver.data.xml.impl.ClassListData;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.instancemanager.AntiFeedManager;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassInfo;
import org.l2j.gameserver.model.eventengine.AbstractEventManager;
import org.l2j.gameserver.model.eventengine.ScheduleTarget;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogout;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.network.serverpackets.olympiad.*;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.util.Objects.*;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.*;
import static org.l2j.gameserver.engine.olympiad.OlympiadState.*;
import static org.l2j.gameserver.network.NpcStringId.CHARACTER_S1_HAS_BECOME_A_HERO_CLASS_S2_CONGRATULATIONS;
import static org.l2j.gameserver.network.SystemMessageId.*;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * @author JoeAlisson
 */
public class Olympiad extends AbstractEventManager<OlympiadMatch> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Olympiad.class);

    private static final int MATCH_HISTORY_PAGE_SIZE = 20;
    private static final DateTimeFormatter HISTORY_DATE_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("uuuu").appendLiteral("year ")
            .appendPattern("MM").appendLiteral("month ")
            .appendPattern("dd").appendLiteral("day ")
            .appendPattern("hh").appendLiteral("hour ")
            .appendPattern("mm").appendLiteral("minute").toFormatter();

    private final IntMap<OlympiadMatch> matches = new CHashIntMap<>(10);
    private final IntMap<OlympiadParticipantData> participantsData = new HashIntMap<>();
    private final IntMap<OlympiadHistoryData> history = new HashIntMap<>();
    private final IntMap<LimitedQueue<OlympiadBattleRecord>> recentBattlesRecord = new HashIntMap<>();

    private Set<Player> registered = ConcurrentHashMap.newKeySet(20);
    private Set<Player> matchMaking = ConcurrentHashMap.newKeySet(20);

    private ConsumerEventListener logoutListener;
    private OlympiadData data = new OlympiadData();
    private OlympiadState state = OlympiadState.SCHEDULED;
    private OlympiadSettings settings;

    private Olympiad() {
    }

    @Override
    public void config(GameXmlReader reader, Node configNode) {
        settings = OlympiadSettings.parse(reader, configNode);
    }

    @Override
    public void onInitialized() {
        data = getDAO(OlympiadDAO.class).findData();

        if(isNull(data)) {
            data = new OlympiadData();
            data.setNextSeasonDate(settings.startDate);
            data.setId(1);
            getDAO(OlympiadDAO.class).save(data);
        } else if(settings.forceStartDate || isNull(data.getNextSeasonDate())) {
            data.setNextSeasonDate(settings.startDate);
        }

        if(data.getNextSeasonDate().isAfter(LocalDate.now())) {
            LOGGER.info("World Olympiad season start scheduled to {}", data.getNextSeasonDate());
        } else if(data.getSeason() > 0) {
            LOGGER.info("World Olympiad {} season has been started", data.getSeason());
        }

        AntiFeedManager.getInstance().registerEvent(AntiFeedManager.OLYMPIAD_ID);

        var listeners = Listeners.players();
        ConsumerEventListener loginListener = new ConsumerEventListener(listeners, EventType.ON_PLAYER_LOGIN, (Consumer<OnPlayerLogin>) this::onPlayerLogin, this);
        listeners.addListener(loginListener);
    }

    @ScheduleTarget
    public void onStartMatch() {
        if(data.getNextSeasonDate().isAfter(LocalDate.now()) || data.getSeason() < 1) {
            return;
        }

        var scheduler = getScheduler("stop-match");
        if(nonNull(scheduler)) {
            state = STARTED;
            logoutListener = new ConsumerEventListener(null, EventType.ON_PLAYER_LOGOUT, (Consumer<OnPlayerLogout>) this::onPlayerLogout, this);

            Broadcast.toAllOnlinePlayers(ExOlympiadInfo.show(OlympiadRuleType.CLASSLESS, (int) scheduler.getRemainingTime(TimeUnit.SECONDS)),
                                    getSystemMessage(SystemMessageId.SHARPEN_YOUR_SWORDS_TIGHTEN_THE_STITCHING_IN_YOUR_ARMOR_AND_MAKE_HASTE_TO_A_OLYMPIAD_MANAGER_BATTLES_IN_THE_OLYMPIAD_GAMES_ARE_NOW_TAKING_PLACE));
        } else {
            LOGGER.warn("Can't find stop-match scheduler");
        }
    }

    private void onPlayerLogin(OnPlayerLogin event) {
        final var player = event.getPlayer();
        showOlympiadUI(player);
        if(player.isHero()) {
            giveHeroSkills(player);
        }
    }

    void showOlympiadUI(Player player) {
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
        if(!state.matchesInProgress()) {
            return;
        }
        state = SCHEDULED;
        Broadcast.toAllOnlinePlayers(ExOlympiadInfo.hide(OlympiadRuleType.CLASSLESS), getSystemMessage(SystemMessageId.THE_OLYMPIAD_REGISTRATION_PERIOD_HAS_ENDED));
        matchMaking.clear();
        registered.clear();
        updateParticipantsData();
    }

    private void updateParticipantsData() {
        if(matches.isEmpty()) {
            var participants = participantsData.values();
            participants.forEach(OlympiadParticipantData::resetBattlesToday);
            getDAO(OlympiadDAO.class).save(participants);
        } else {
            ThreadPool.schedule(this::updateParticipantsData, 1, TimeUnit.MINUTES);
        }
    }

    @ScheduleTarget
    public void onNewSeason(){
        if(LocalDate.now().compareTo(data.getNextSeasonDate()) >= 0) {
            final var olympiadDAO = getDAO(OlympiadDAO.class);

            processHeroes(olympiadDAO);
            processRank(olympiadDAO);

            participantsData.clear();
            history.clear();

            data.increaseSeason();
            olympiadDAO.save(data);
            Broadcast.toAllOnlinePlayers(getSystemMessage(SystemMessageId.ROUND_S1_OF_THE_OLYMPIAD_GAMES_HAS_STARTED).addInt(data.getSeason()));
        }
    }

    private void processRank(OlympiadDAO olympiadDAO) {
        olympiadDAO.saveRankSnapshot(settings.saveCycleMinBattles);
        olympiadDAO.deletePreviousRankSnapshot();
        olympiadDAO.saveRankClassSnapshot(settings.saveCycleMinBattles);
        olympiadDAO.deletePreviousRankClassSnapshot();

        if(settings.enableLegend) {
            olympiadDAO.updateLegend();
            olympiadDAO.updateLegendHistory();
        }

        olympiadDAO.updateOlympiadHistory(data.getSeason());
        olympiadDAO.deleteParticipants();
    }

    private void processHeroes(OlympiadDAO olympiadDAO) {
        removeOnlineHeroes(olympiadDAO);
        olympiadDAO.deleteHeroes();
        olympiadDAO.saveHeroes(settings.minBattlesWonToBeHero);
        olympiadDAO.updateHeroesHistory();
        olympiadDAO.deleteHeroesMatches();
        olympiadDAO.saveHeroesMatches();
        olympiadDAO.deleteMatches();
    }

    private void removeOnlineHeroes(OlympiadDAO olympiadDAO) {
        IntSet heroesIds = olympiadDAO.findHeroesId(ServerSettings.serverId());
        heroesIds.forEach(this::removeHero);
    }

    private void removeHero(int heroId) {
        final var player = World.getInstance().findPlayer(heroId);
        if(nonNull(player)) {
            player.setHero(false);
            player.broadcastUserInfo(UserInfoType.SOCIAL);

            for (Skill skill : settings.heroSkills) {
                player.removeSkill(skill, false);
            }
        }
    }

    public void showHeroList(Player player) {
        player.sendPacket(new ExHeroList(getDAO(OlympiadDAO.class).findRankHeroes()));
    }

    public boolean isMatchesInProgress() {
        return state.matchesInProgress();
    }

    public int getCurrentSeason() {
        return data.getSeason();
    }

    public void saveOlympiadStatus() {
        getDAO(OlympiadDAO.class).save(data);
    }

    public short getOlympiadPoints(Player player) {
        return participantDataOf(player).getPoints();
    }

    private int getRemainingDailyMatches(Player player) {
        return settings.maxBattlesPerDay - participantDataOf(player).getBattlesToday();
    }

    public short getMaxBattlesPerDay() {
        return settings.maxBattlesPerDay;
    }

    public void requestMatchMaking(Player player) {
        if (!validateMatchMaking(player)) {
            return;
        }

        var htmlMessage = new NpcHtmlMessage(HtmCache.getInstance().getHtm(player, "data/html/olympiad/match-making.htm"));
        htmlMessage.replace("%stats%", stats());
        player.sendPacket(htmlMessage);
    }

    public boolean checkLevelAndClassRestriction(Player player) {
        return player.getLevel() >= settings.minLevel && player.getClassId().level() >= settings.minClassLevel;
    }

    private boolean validateMatchMaking(Player player) {
        if(!state.matchesInProgress())  {
            player.sendPacket(THE_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
            return false;
        }

        if(!checkLevelAndClassRestriction(player)) {
            player.sendPacket(YOU_MUST_LEVEL_70_OR_HIGHER_AND_HAVE_COMPLETED_THE_2ND_CLASS_TRANSFER_IN_ORDER_TO_PARTICIPATE_IN_A_MATCH);
            return false;
        }

        if(getRemainingDailyMatches(player) < 1) {
            player.sendPacket(YOU_VE_USED_UP_ALL_YOUR_MATCHES);
            return false;
        }

        if(player.isInInstance() || player.isInTimedHuntingZone()) {
            player.sendPacket(CANNOT_APPLY_TO_PARTICIPATE_IN_A_MATCH_WHILE_IN_AN_INSTANCED_ZONE);
            return false;
        }

        if(player.isDead()) {
            player.sendPacket(CANNOT_APPLY_TO_PARTICIPATE_IN_A_MATCH_WHILE_DEAD);
            return false;
        }

        if(player.isOnEvent()) {
            player.sendPacket(YOU_MAY_NOT_PARTICIPATE_AS_YOU_ARE_CURRENTLY_PARTICIPATING_IN_ANOTHER_PVP_MATCH);
            return false;
        }

        if(player.isInOlympiadMode() || registered.contains(player)) {
            player.sendPacket(getSystemMessage(C1_YOU_HAVE_ALREADY_REGISTERED_FOR_THE_MATCH).addPcName(player));
            return false;
        }

        if(player.getReputation() < 0 || getOlympiadPoints(player) <= 0) {
            player.sendPacket(YOU_CANNOT_ENTER_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);// TODO find the retail message
            return false;
        }

        if(player.isFishing()){
            player.sendPacket(YOU_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD_WHILE_FISHING);
            return false;
        }

        if(!player.isInventoryUnder80()) {
            player.sendPacket(CANNOT_APPLY_TO_PARTICIPATE_BECAUSE_YOUR_INVENTORY_SLOTS_OR_WEIGHT_EXCEEDED_80);
            return false;
        }
        return true;
    }

    private String stats() {
        return String.format("(period: %d, week: %d, number of participants: %d)", data.getSeason(),  LocalDate.now().get(ChronoField.ALIGNED_WEEK_OF_YEAR) , getParticipantsCount());
    }

    public int getParticipantsCount() {
        return registered.size() + matchMaking.size() + matches.size() * 2;
    }

    public void startMatchMaking(Player player) {
        if(!validateMatchMaking(player)) {
            return;
        }

        registered.add(player);
        player.addListener(logoutListener);
        player.sendPacket(YOU_VE_BEEN_REGISTERED_IN_THE_WAITING_LIST_OF_ALL_CLASS_BATTLE);
        showRecord(player);
        player.sendPackets(new ExOlympiadMatchMakingResult(true, OlympiadRuleType.CLASSLESS));

        if(state != MATCH_MAKING && registered.size() >= settings.minParticipant) {
            state = MATCH_MAKING;
            ThreadPool.schedule(this::distributeAndStartMatches, 1, TimeUnit.MINUTES);
        }
    }

    private void distributeAndStartMatches() {
        var tmp = matchMaking;
        matchMaking = registered;
        registered = tmp;

        var participants = matchMaking.iterator();
        while(matchMaking.size() >= OlympiadRuleType.CLASSLESS.participantCount()) {
            newMatch(participants);
        }

        if(!matchMaking.isEmpty()) {
            registered.addAll(matchMaking);
            matchMaking.clear();
        }

        if(state == MATCH_MAKING) {
            state = STARTED;
        }
    }

    private void newMatch(Iterator<Player> participants) {
        OlympiadMatch match = OlympiadMatch.of(OlympiadRuleType.CLASSLESS);
        var arena = InstanceManager.getInstance().createInstance(Rnd.get(settings.availableArenas));
        match.setArenaInstance(arena);
        match.setMatchDuration(settings.matchDuration);

        for (int i = 0; i < OlympiadRuleType.CLASSLESS.participantCount(); i++) {
            var participant = participants.next();
            match.addParticipant(participant);
            participants.remove();
        }

        ThreadPool.schedule(match, 10, TimeUnit.SECONDS);
    }

    void startMatch(OlympiadMatch match) {
        matches.put(match.getId(), match);
        match.forEachParticipant(this::removeLogoutListener);
    }

    private void removeLogoutListener(Player player) {
        player.removeListener(logoutListener);
    }

    void finishMatch(OlympiadMatch match) {
        matches.remove(match.getId());
    }

    public void unregisterPlayer(Player player) {
        unregisterPlayer(player, OlympiadRuleType.CLASSLESS);
    }

    public void unregisterPlayer(Player player, OlympiadRuleType ruleType) {
        player.removeListener(logoutListener);
        if(registered.remove(player)) {
            player.sendPacket(YOU_HAVE_BEEN_REMOVED_FROM_THE_OLYMPIAD_WAITING_LIST);
            player.sendPacket(new ExOlympiadMatchMakingResult(false, ruleType));
            showRecord(player);
        }
    }

    public boolean isRegistered(Player player) {
        return player.isInOlympiadMode() || registered.contains(player);
    }

    protected void onPlayerLogout(OnPlayerLogout event) {
        final var player = event.getPlayer();
        LOGGER.warn("player has logged out while in Olympiad");
        unregisterPlayer(player, OlympiadRuleType.CLASSLESS);
        matchMaking.remove(player);
        applyDismissPenalty(player);
    }

    void applyDismissPenalty(Player player) {
        final var data = participantDataOf(player);
        data.updatePoints(data.getPoints() / -5);
        getDAO(OlympiadDAO.class).save(data);
    }

    public int getSeasonMonth() {
        return YearMonth.now().getMonthValue();
    }

    public int getSeasonYear() {
        return Year.now().getValue();
    }

    public void showRecord(Player player) {
        if(player.isInOlympiadMode()) {
            player.sendPacket(DURING_BATTLE_OR_VIEWING_OLYMPIAD_WINDOW_CANNOT_BE_OPENED);
            return;
        }

        player.sendPacket(new ExOlympiadRecord(participantDataOf(player), lastCycleDataOf(player)));
    }

    private OlympiadHistoryData lastCycleDataOf(Player player) {
        if(!checkLevelAndClassRestriction(player)) {
            return OlympiadHistoryData.DEFAULT;
        }
        var data = history.get(player.getObjectId());
        if(isNull(data)) {
            data = loadOrDefaultHistory(player);
        }
        return data;
    }

    private OlympiadHistoryData loadOrDefaultHistory(Player player) {
        OlympiadHistoryData historyData = getLastCycleInfo(player.getObjectId());
        if(isNull(historyData)) {
            historyData = OlympiadHistoryData.DEFAULT;
        }
        history.put(player.getObjectId(), historyData);
        return historyData;
    }

    private OlympiadHistoryData getLastCycleInfo(int playerId) {
        return getDAO(OlympiadDAO.class).findHistory(playerId, ServerSettings.serverId(), data.getSeason() - 1);
    }

    private OlympiadParticipantData participantDataOf(Player player) {
        if(!checkLevelAndClassRestriction(player)) {
            return OlympiadParticipantData.DEFAULT;
        }
        var data = participantsData.get(player.getObjectId());
        if(isNull(data)) {
            data = loadOrCreateParticipantData(player);
        }
        return data;
    }

    private OlympiadParticipantData loadOrCreateParticipantData(Player player) {
        var participantData = getDAO(OlympiadDAO.class).findParticipantData(player.getObjectId(), ServerSettings.serverId());
        if(isNull(participantData)) {
            participantData = OlympiadParticipantData.of(player, settings.initialPoints, ServerSettings.serverId());
            getDAO(OlympiadDAO.class).save(participantData);
        }
        participantsData.put(player.getObjectId(), participantData);
        return participantData;
    }

    short updateDefeat(Player player, int points, Player winnerLeader, Duration battleDuration) {
        var data = participantDataOf(player);
        data.updatePoints(points);
        data.increaseDefeats();
        data.increaseBattlesToday();

        final int server =  ServerSettings.serverId();
        getDAO(OlympiadDAO.class).updateDefeat(player.getObjectId(), server, data.getPoints(), data.getBattlesToday());
        getDAO(OlympiadDAO.class).save(OlympiadMatchResultData.of(player, server, winnerLeader, PlayerMatchResult.LOSS, data, battleDuration));
        recentBattlesRecord.computeIfAbsent(player.getObjectId(), i -> new LimitedQueue<>(3)).add(new OlympiadBattleRecord(winnerLeader.getName(), winnerLeader.getClassId().getId(), winnerLeader.getLevel(), (byte) 0));
        return data.getPoints();
    }

    short updateVictory(Player player, int points, Player loserLeader, Duration battleDuration) {
        var data = participantDataOf(player);
        data.updatePoints(points);
        data.increaseVictory();
        data.increaseBattlesToday();
        final int server = ServerSettings.serverId();
        getDAO(OlympiadDAO.class).updateVictory(player.getObjectId(), server, data.getPoints(), data.getBattlesToday());
        getDAO(OlympiadDAO.class).save(OlympiadMatchResultData.of(player, server, loserLeader, PlayerMatchResult.VICTORY, data, battleDuration));
        recentBattlesRecord.computeIfAbsent(player.getObjectId(), i -> new LimitedQueue<>(3)).add(new OlympiadBattleRecord(loserLeader.getName(), loserLeader.getClassId().getId(), loserLeader.getLevel(), (byte) 1));
        return data.getPoints();
    }

    short updateTie(Player player, Player enemy, Duration battleDuration) {
        var data = participantDataOf(player);
        data.increaseBattlesToday();

        final int server = ServerSettings.serverId();
        getDAO(OlympiadDAO.class).updateTie(player.getObjectId(), server, data.getPoints(), data.getBattlesToday());
        getDAO(OlympiadDAO.class).save(OlympiadMatchResultData.of(player, server, enemy, PlayerMatchResult.DRAW, data, battleDuration));
        recentBattlesRecord.computeIfAbsent(player.getObjectId(), i -> new LimitedQueue<>(3)).add(new OlympiadBattleRecord(enemy.getName(), enemy.getClassId().getId(), enemy.getLevel(), (byte) 2));
        return data.getPoints();
    }

    int getBattlePoints(List<OlympiadResultInfo> loserTeam) {
        int points =  Rnd.get(settings.minBattlePoints, settings.maxBattlePoints);
        for (OlympiadResultInfo resultInfo : loserTeam) {
            var data = participantDataOf(resultInfo.getPlayer());
            points = Math.min(data.getPoints(), points);
        }
        return points;
    }

    public void showPersonalRank(Player player) {
        final var server = ServerSettings.serverId();
        final var rankData = getDAO(OlympiadDAO.class).findRankData(player.getObjectId(), server);
        final var previousData = getDAO(OlympiadDAO.class).findPreviousRankData(player.getObjectId(), server);
        player.sendPacket(new ExOlympiadMyRankInfo(rankData, previousData, recentBattlesRecord.get(player.getObjectId())));
    }

    public void showRanking(Player player, byte type, byte scope, boolean currentSeason, int classId, int server) {
        final List<OlympiadRankData> data = currentSeason ? getCurrentRankers(player, type, scope, classId, server) : getPreviousRankers(player, type, scope, classId, server);
        final int participants = currentSeason ? getDAO(OlympiadDAO.class).countParticipants() : getDAO(OlympiadDAO.class).countPreviousParticipants();
        player.sendPacket(new ExOlympiadRankingInfo(type, scope, currentSeason, classId, server, data, participants));
    }

    private List<OlympiadRankData> getPreviousRankers(Player player, byte type, byte scope, int classId, int server) {
        final var dao = getDAO(OlympiadDAO.class);
        if(type == 1) {
            return scope == 1 ? dao.findPreviousRankersNextToPlayerByClass(player.getObjectId(), classId) : dao.findPreviousRankersByClass(classId, server);
        }
        return scope == 1 ? dao.findPreviousRankersNextToPlayer(player.getObjectId()) : dao.findPreviousRankers();
    }

    private List<OlympiadRankData> getCurrentRankers(Player player, byte type, byte scope, int classId, int server) {
        final var dao = getDAO(OlympiadDAO.class);

        if(type == 1) {
            return scope == 1 ? dao.findRankersNextToPlayerByClass(player.getObjectId(), classId) : dao.findRankersByClass(classId, server);
        }

        return scope == 1 ? dao.findRankersNextToPlayer(player.getObjectId()) : dao.findRankers();
    }

    public void showRankingHeroes(Player player) {
        final var dao = getDAO(OlympiadDAO.class);
        player.sendPacket(new ExOlympiadHeroesInfo(dao.findRankLegend(), dao.findRankHeroes()));
    }

    public boolean isMatchInBattle(int matchId) {
        return falseIfNullOrElse(matches.get(matchId), OlympiadMatch::isInBattle);
    }

    public void sendPacketToMatch(int matchId, ServerPacket packet) {
        var match = matches.get(matchId);
        if(nonNull(match)) {
            match.sendPacket(packet);
        }
    }

    public boolean claimHero(Player player) {
        if(isUnclaimedHero(player)) {
            player.setHero(true);

            player.broadcastUserInfo(UserInfoType.SOCIAL);
            addClanReputation(player);
            getDAO(OlympiadDAO.class).claimHero(player.getObjectId(), ServerSettings.serverId());

            player.broadcastPacket(PlaySound.music("ns01_f"));
            final var className =  ClassListData.getInstance().getClass(player.getClassId()).getClassName();
            showOnScreenMsg(player, CHARACTER_S1_HAS_BECOME_A_HERO_CLASS_S2_CONGRATULATIONS, ExShowScreenMessage.TOP_CENTER, 5000, player.getName(), className);
            player.broadcastPacket(new SocialAction(player.getObjectId(), SocialAction.HERO_CLAIMED));

            for (var reward : settings.heroRewards) {
                player.getInventory().addItem("Olympiad", reward.getId(), reward.getCount(), null, null);
            }

            giveHeroSkills(player);

            return true;
        }
        return false;
    }

    private void giveHeroSkills(Player player) {
        for (var skill : settings.heroSkills) {
            player.addSkill(skill, false);
        }
    }

    public boolean isUnclaimedHero(Player player) {
        return getDAO(OlympiadDAO.class).isUnclaimedHero(player.getObjectId(), ServerSettings.serverId());
    }

    public boolean isHero(int playerId) {
        return getDAO(OlympiadDAO.class).isHero(playerId, ServerSettings.serverId());
    }

    private void addClanReputation(Player player) {
        final Clan clan = player.getClan();
        if (settings.heroReputation > 0 && nonNull(clan) && clan.getLevel() >= 5) {
            clan.addReputationScore(settings.heroReputation, true);
            player.sendPacket(getSystemMessage(YOUR_CLAN_HAS_ADDED_S1_POINT_S_TO_ITS_CLAN_REPUTATION).addInt(settings.heroReputation));
            clan.broadcastToOtherOnlineMembers(getSystemMessage(SystemMessageId.CLAN_MEMBER_C1_WAS_NAMED_A_HERO_S2_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION)
                    .addString(player.getName()).addInt(settings.heroReputation), player);
        }
    }

    public void showMatchList(Player player) {
        player.sendPacket(new ExOlympiadMatchList(matches.values()));
    }

    public void showHeroHistory(Player player, int classId, int page) {
        final var matchesResult = getDAO(OlympiadDAO.class).findHeroHistoryByClassId(classId);
        if(!isNullOrEmpty(matchesResult)) {
            player.sendPacket( createHeroHistory(player, matchesResult, classId, page) );
        } else {
            LOGGER.warn("No matches result to class id {} requested by {}", classId, player);
        }
    }

    private NpcHtmlMessage createHeroHistory(Player player, List<OlympiadMatchResultData> matchesResult, int classId, int page) {
        final StringBuilder sb = formatMatches(player, matchesResult, page);

        final var heroId = matchesResult.get(0).getPlayerId();
        var heroHistory = history.get(heroId);
        if(isNull(heroHistory)) {
            heroHistory = requireNonNullElse(getLastCycleInfo(heroId), OlympiadHistoryData.DEFAULT);
        }

        final var message = new NpcHtmlMessage(HtmCache.getInstance().getHtm(player, "data/html/olympiad/hero-history.htm"));
        message.replace("%battles%", sb.toString());
        message.replace("%win%", heroHistory.getBattlesOwn());
        message.replace("%tie%", heroHistory.getBattles() - heroHistory.getBattlesOwn() - heroHistory.getBattlesLost());
        message.replace("%loss%", heroHistory.getBattlesLost());

        if (page > 1) {
            message.replace("%previous%", "<button value=\"Prev\" action=\"bypass _match?class=" + classId + "&page=" + (page - 1) + "\" width=60 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
        } else {
            message.replace("%previous%", "");
        }

        if (matchesResult.size() > (page - 1) * MATCH_HISTORY_PAGE_SIZE + MATCH_HISTORY_PAGE_SIZE) {
            message.replace("%next%", "<button value=\"Next\" action=\"bypass _match?class=" + classId + "&page=" + (page + 1) + "\" width=60 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
        } else {
            message.replace("%next%", "");
        }
        return message;
    }

    private StringBuilder formatMatches(Player player, List<OlympiadMatchResultData> matchesResult, int page) {
        final var sb = new StringBuilder();
        final var battleTemplate = HtmCache.getInstance().getHtm(player, "data/html/olympiad/hero-history-battle.htm");

        final int initial = (page -1) * MATCH_HISTORY_PAGE_SIZE;
        for (int i = initial; i < Math.min(initial + MATCH_HISTORY_PAGE_SIZE, matchesResult.size()) ; i++) {
            var battle = matchesResult.get(i);
            sb.append(battleTemplate.replace("%date%", battle.getDate().format(HISTORY_DATE_FORMATTER))
                    .replace("%opponent%", battle.getOpponentName())
                    .replace("%class_name%", emptyIfNullOrElse(ClassListData.getInstance().getClass(battle.getOpponentClassId()), ClassInfo::getClassName))
                    .replace("%duration%", battle.getDuration().toMinutes() + " minutes " + battle.getDuration().toSecondsPart() + "seconds")
                    .replace("%result%", formattedResult(battle.getResult()))
                    .replace("%win%", String.valueOf(battle.getWin()))
                    .replace("%tie%", String.valueOf(battle.getTie()))
                    .replace("%loss%", String.valueOf(battle.getLoss()))
            );
        }
        return sb;
    }

    private String formattedResult(PlayerMatchResult result) {
        return switch(result) {
            case VICTORY -> "<font color=\"125AC7\">victory</font>";
            case LOSS -> "<font color=\"CC0000\">loss</font>";
            case DRAW -> "draw";
        };
    }

    public void addSpectator(Player player, int matchId) {
        if(matchId == player.getOlympiadMatchId()) {
            return;
        }

        if(player.isInOlympiadMode()) {
            if(!player.isInObserverMode()) {
                return;
            }
            var previousMatch = matches.get(player.getOlympiadMatchId());
            previousMatch.removeSpetator(player);
        }

        var match = matches.get(matchId);
        if(nonNull(match)) {
            match.addSpectator(player);
        }
    }

    public void removeSpectator(Player player) {
        var match = matches.get(player.getOlympiadMatchId());
        if(nonNull(match)){
            match.removeSpetator(player);
        }
    }

    void giveWinnerRewards(Player player) {
        for (var reward : settings.winnerRewards) {
            player.getInventory().addItem("Olympiad", reward.getId(), reward.getCount(), null, null);
        }
    }

    public void giveLoserRewards(Player player) {
        for (var reward : settings.loserRewards) {
            player.getInventory().addItem("Olympiad", reward.getId(), reward.getCount(), null, null);
        }
    }

    public void giveTieRewards(Player player) {
        for (var reward : settings.tieRewards) {
            player.getInventory().addItem("Olympiad", reward.getId(), reward.getCount(), null, null);
        }
    }

    boolean keepDances() {
        return settings.keepDance;
    }

    public int getUnclaimedPoints(Player player) {
        return getDAO(OlympiadDAO.class).unclaimedPoints(player.getObjectId(), ServerSettings.serverId());
    }

    public void changePoints(Player player) {
        if(!player.isInventoryUnder80()) {
            player.sendPacket(SystemMessageId.UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
            return;
        }

        int points = getUnclaimedPoints(player);

        if(points <= 0) {
            return;
        }

        points += getRankingPoints(player);

        giveItems(player, settings.markOfBattle, points * settings.markOfBattlePerPoint);
        getDAO(OlympiadDAO.class).claimPoints(player.getObjectId(), ServerSettings.serverId());
    }

    private int getRankingPoints(Player player) {
        final var history = lastCycleDataOf(player);
        final double ranking = history.getOverallRank() / (double) history.getOverallCount() * 100;
        int points = 0;

        for (var rankingReward : settings.rankingRewards) {
            if(rankingReward.contains(ranking)) {
                points = (int) rankingReward.value();
                break;
            }
        }
        if(isHero(player.getObjectId())) {
            points += settings.markOfBattleIfHero;
        } else if(history.getBattlesOwn() > 1) {
            points += settings.markOfBattleIfWin;
        }
        return points;
    }

    public static Olympiad getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final Olympiad INSTANCE = new Olympiad();
    }

}
