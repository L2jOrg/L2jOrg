package org.l2j.gameserver.network.serverpackets.rank;

import org.l2j.gameserver.data.database.RankManager;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExRankingCharInfo extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client) {
        var player = client.getPlayer();
        var rankManager = RankManager.getInstance();
        var rank = rankManager.getRank(player);
        var snapshot = rankManager.getSnapshot(player);

        writeId(ServerPacketId.EX_RANKING_CHAR_INFO);
        writeInt(rank.getRank()); // server rank
        writeInt(rank.getRankRace()); // race rank
        writeInt(snapshot.getRank()); // server rank snapshot
        writeInt(snapshot.getRankRace()); // race rank snapshot
    }
}
