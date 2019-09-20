package org.l2j.gameserver.network.serverpackets.rank;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JoeAlisson
 */
public class ExRankList extends ServerPacket {

    private final int race;
    private final byte group;
    private final byte scope;

    public ExRankList(byte group, byte scope, int race) {
        this.group = group;
        this.scope = scope;
        this.race = race;
    }

    @Override
    protected void writeImpl(GameClient client)  {
        writeId(ServerPacketId.EX_RANKING_CHAR_RANKERS);
        writeByte(group);
        writeByte(scope);
        writeInt(race);

       List<Ranker> rankers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            addRanker(rankers);
        }

        writeInt(rankers.size());

        for (Ranker ranker : rankers) {
            writeString(ranker.name);
            writeString(ranker.pledgeName);
            writeInt(ranker.level);
            writeInt(ranker.rClass);
            writeInt(ranker.rank);
            writeInt(ranker.raceRankSnapshot);
            writeInt(ranker.serverRankSnapshot);
        }
    }


    private static void addRanker(List<Ranker>  rankers) {
        var ranker = new Ranker();
        ranker.name = "Ranker" + rankers.size();
        ranker.pledgeName = "ClanRanker" + rankers.size();
        ranker.level = 20;
        ranker.rClass = rankers.size();
        ranker.rank = 1+rankers.size();
        ranker.raceRankSnapshot = ranker.rank + rankers.size();
        ranker.serverRankSnapshot = ranker.rank + rankers.size() + 1;

        rankers.add(ranker);
    }

    private static class Ranker {
        String name;
        String pledgeName;
        int level;
        int rClass;
        int rank;
        int serverRankSnapshot;
        int raceRankSnapshot;
    }
}
