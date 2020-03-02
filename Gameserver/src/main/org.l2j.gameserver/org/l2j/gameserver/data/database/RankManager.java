package org.l2j.gameserver.data.database;

import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.data.database.dao.RankDAO;
import org.l2j.gameserver.data.database.data.RankData;
import org.l2j.gameserver.data.database.data.RankHistoryData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.world.World;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.doIfNonNull;

/**
 * @author JoeAlisson
 */
public class RankManager {

    private IntMap<RankData> rankersSnapshot;

    private RankManager() {
        var listeners = Listeners.players();

        listeners.addListener(new ConsumerEventListener(listeners, EventType.ON_PLAYER_LOGIN, (Consumer<OnPlayerLogin>) (event) -> {
            var player = event.getPlayer();
            var rankData = rankersSnapshot.get(player.getObjectId());
            if(nonNull(rankData)) {
                doIfNonNull(getRankerSkill(rankData), s -> player.addSkill(s.getSkill()));
                doIfNonNull(getRaceRankerSkill(rankData, player), s -> player.addSkill(s.getSkill()));
            }
        }, this));
    }

    private void loadRankers() {
        rankersSnapshot = getDAO(RankDAO.class).findAllSnapshot();
    }

    public void updateRankers() {
        rankersSnapshot.values().forEach(this::removeRankerSkills);
        updateDatabase();
        loadRankers();
    }

    private void removeRankerSkills(RankData rankData) {
        var player = World.getInstance().findPlayer(rankData.getPlayerId());
        if(nonNull(player)) {
            doIfNonNull(getRankerSkill(rankData), s -> player.removeSkill(s.getId(), true));
            doIfNonNull(getRaceRankerSkill(rankData, player), s -> player.removeSkill(s.getId(), true));
        }
    }

    private CommonSkill getRaceRankerSkill(RankData rankData, Player player) {
        int rank = rankData.getRankRace();
        if(rank == 1) {
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

    private CommonSkill getRankerSkill(RankData rankData) {
        var rank = rankData.getRank();
        CommonSkill skill = null;
        if(rank == 1) {
            skill = CommonSkill.RANKER_FIRST_CLASS;
        } else if(rank <= 30) {
            skill = CommonSkill.RANKER_SECOND_CLASS;
        } else if(rank <= 100) {
            skill = CommonSkill.RANKER_THIRD_CLASS;
        }
        return skill;
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
