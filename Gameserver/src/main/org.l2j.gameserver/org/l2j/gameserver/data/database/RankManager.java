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
package org.l2j.gameserver.data.database;

import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.data.database.dao.RankDAO;
import org.l2j.gameserver.data.database.data.RankData;
import org.l2j.gameserver.data.database.data.RankHistoryData;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerPeaceZoneEnter;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerPeaceZoneExit;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.network.serverpackets.rank.ExBowAction;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.zone.ZoneType;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.doIfNonNull;

/**
 * @author JoeAlisson
 */
public class RankManager {

    private IntMap<RankData> rankersSnapshot;
    private ScheduledFuture<?> bowTask;

    private RankManager() {
        var listeners = Listeners.players();
        listeners.addListener(new ConsumerEventListener(listeners, EventType.ON_PLAYER_LOGIN, (Consumer<OnPlayerLogin>) e -> addRankersSkills(e.getPlayer()), this));
        listeners.addListener(new ConsumerEventListener(listeners, EventType.ON_PLAYER_PEACE_ZONE_ENTER, (Consumer<OnPlayerPeaceZoneEnter>) this::scheduleBow, this));
        listeners.addListener(new ConsumerEventListener(listeners, EventType.ON_PLAYER_PEACE_ZONE_EXIT, (Consumer<OnPlayerPeaceZoneExit>) this::cancelBowTask, this));
    }

    private void cancelBowTask(OnPlayerPeaceZoneExit event) {
        if(event.getPlayer().getRank() == 1 && nonNull(bowTask)) {
            bowTask.cancel(false);
            bowTask = null;
        }
    }

    private void scheduleBow(OnPlayerPeaceZoneEnter event) {
        var player = event.getPlayer();
        if(player.getRank() == 1 && event.getZone().getPlayersInsideCount() > 1 &&  (isNull(bowTask) || bowTask.isCancelled())) {
            bowTask = ThreadPool.scheduleAtFixedDelay(() -> Broadcast.toSelfAndKnownPlayersInRadius(player, new ExBowAction(player), 1000, p -> p.isInsideZone(ZoneType.PEACE)), 20, 180, TimeUnit.SECONDS);
        }
    }

    private void addRankersSkills(Player player) {
        var rankData = rankersSnapshot.get(player.getObjectId());

        if(isNull(rankData)) {
            return;
        }

        player.setRank(rankData.getRank());
        player.setRankRace(rankData.getRankRace());

        var rank = player.getRank();

        CommonSkill skill = null;
        if(rank <= 100) {
            player.addSkill(CommonSkill.RANKER_BENEFIT_I.getSkill());
            skill = CommonSkill.RANKER_THIRD_CLASS;
        }

        if(rank <= 30) {
            player.addSkill(CommonSkill.RANKER_BENEFIT_II.getSkill());
            skill = CommonSkill.RANKER_SECOND_CLASS;
        }

        if(rank == 1) {
            player.addSkill(CommonSkill.RANKER_BENEFIT_III.getSkill());
            skill = CommonSkill.RANKER_FIRST_CLASS;
        }

        if(nonNull(skill)) {
            skill.getSkill().applyEffects(player, player);
        }

        if(player.getRankRace() == 1) {
            player.addSkill(CommonSkill.RANKER_RACE_BENEFIT.getSkill());
            doIfNonNull(getRaceRankerSkill(player), s -> s.getSkill().applyEffects(player, player));
        }
        if(player.getRank() == 1 || player.getRankRace() == 1){
            player.sendPacket(new UserInfo(player, UserInfoType.RANKER));
        }
    }

    private void loadRankers() {
        rankersSnapshot = getDAO(RankDAO.class).findAllSnapshot();
    }

    public void updateRankers() {
        rankersSnapshot.values().forEach(this::removeRankerSkills);
        updateDatabase();
        loadRankers();
        rankersSnapshot.values().forEach(this::addRankersSkills);
    }

    private void addRankersSkills(RankData rankData) {
        doIfNonNull(World.getInstance().findPlayer(rankData.getPlayerId()), this::addRankersSkills);
    }

    private void removeRankerSkills(RankData rankData) {
        doIfNonNull(World.getInstance().findPlayer(rankData.getPlayerId()), player -> {
            removeRankersSkill(player);
            player.setRank(0);
            player.setRankRace(0);
        });
    }

    private void removeRankersSkill(Player player) {
        player.removeSkill(CommonSkill.RANKER_BENEFIT_I.getId(), true);
        player.removeSkill(CommonSkill.RANKER_BENEFIT_II.getId(), true);
        player.removeSkill(CommonSkill.RANKER_BENEFIT_III.getId(), true);
        player.removeSkill(CommonSkill.RANKER_RACE_BENEFIT.getId(), true);
        player.stopSkillEffects(true, CommonSkill.RANKER_FIRST_CLASS.getId());
        player.stopSkillEffects(true, CommonSkill.RANKER_SECOND_CLASS.getId());
        player.stopSkillEffects(true, CommonSkill.RANKER_THIRD_CLASS.getId());
        doIfNonNull(getRaceRankerSkill(player), s -> player.stopSkillEffects(true, s.getId()));
    }

    private CommonSkill getRaceRankerSkill(Player player) {
        if(player.getRankRace() == 1) {
           return switch (player.getRace()) {
               case HUMAN -> CommonSkill.RANKER_HUMAN;
               case ELF -> CommonSkill.RANKER_ELF;
               case DARK_ELF -> CommonSkill.RANKER_DARK_ELF;
               case ORC -> CommonSkill.RANKER_ORC;
               case DWARF -> CommonSkill.RANKER_DWARF;
               case JIN_KAMAEL -> CommonSkill.RANKER_JIN_KAMAEL;
               default -> null;
            };
        }
        return null;
    }

    private void updateDatabase() {
        var dao = getDAO(RankDAO.class);
        dao.clearSnapshot();
        dao.updateSnapshot();

        var now = Instant.now();
        dao.updateRankersHistory(now.getEpochSecond());
        dao.removeOldRankersHistory(now.minus(7, ChronoUnit.DAYS).getEpochSecond());
    }

    public RankData getRank(Player player) {
        return getDAO(RankDAO.class).findPlayerRank(player.getObjectId());
    }

    public List<RankData> getRankers() {
        return getDAO(RankDAO.class).findAll();
    }

    public List<RankData> getRaceRankers(int race) {
        return getDAO(RankDAO.class).findAllByRace(race);
    }

    public List<RankData> getClanRankers(int clanId) {
        return getDAO(RankDAO.class).findByClan(clanId);
    }

    public List<RankData> getFriendRankers(Player player) {
        return getDAO(RankDAO.class).findFriendRankers(player.getObjectId());
    }

    public List<RankData> getRankersByPlayer(Player player) {
        return getDAO(RankDAO.class).findRankersNextToPlayer(player.getObjectId());
    }

    public List<RankData> getRaceRankersByPlayer(Player player) {
        return getDAO(RankDAO.class).findRaceRankersNextToPlayer(player.getObjectId(), player.getRace().ordinal());
    }

    public List<RankHistoryData> getPlayerHistory(Player player) {
        return getDAO(RankDAO.class).findPlayerHistory(player.getObjectId());
    }

    public static void init() {
        getInstance().loadRankers();
    }

    public static RankManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final RankManager INSTANCE = new RankManager();
    }
}
