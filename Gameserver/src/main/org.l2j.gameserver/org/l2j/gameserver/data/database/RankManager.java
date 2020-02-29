package org.l2j.gameserver.data.database;

import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.data.database.dao.RankDAO;
import org.l2j.gameserver.data.database.data.RankData;
import org.l2j.gameserver.model.actor.instance.Player;

import java.util.List;

import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author JoeAlisson
 */
public class RankManager {

    private static final int FIRST_PLACE_SKILL_ID = 60003; // 1st
    private static final int SECOND_PLACE_SKILL_ID = 60004; // 2nd - 30th
    private static final int THIRD_PLACE_SKILL_ID = 60005; // 31 - 100th

    private static final int HUMAN_SKILL_ID = 60006;
    private static final int ELF_SKILL_ID = 60007;
    private static final int DARK_ELF_SKILL_ID = 60008;
    private static final int ORC_SKILL_ID = 60009;
    private static final int DWARF_SKILL_ID = 60010;
    private static final int JIN_KAMAEL_SKILL_ID = 60011;

    private static final int FIRST_RANK_SKILL_ID = 60012;
    private static final int SECOND_RANK_SKILL_ID = 60013;
    private static final int THIRD_RANK_SKILL_ID = 60014;

    private static final int AMONG_RACE_SKILL_ID = 60015;

    private IntMap<RankData> snapshotRankers;

    private RankManager() {
    }

    private void loadRankers() {
        snapshotRankers = getDAO(RankDAO.class).findAllSnapshot();
    }

    public void updateRankers() {
        updateDatabase();
        loadRankers();

    }

    private void updateDatabase() {
        var dao = getDAO(RankDAO.class);
        dao.clearSnapshot();
        dao.updateSnapshot();

        dao.clearRaceSnapshot();
        dao.updateRaceSnapshot();

    }

    public RankData getRank(Player player) {
        return getDAO(RankDAO.class).findPlayerRank(player.getObjectId());
    }

    public RankData getSnapshot(Player player) {
        return snapshotRankers.get(player.getObjectId());
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
