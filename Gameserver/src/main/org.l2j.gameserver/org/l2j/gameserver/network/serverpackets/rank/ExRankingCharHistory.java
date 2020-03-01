package org.l2j.gameserver.network.serverpackets.rank;

import org.l2j.gameserver.data.database.RankManager;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExRankingCharHistory extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client) {
        var history = RankManager.getInstance().getPlayerHistory(client.getPlayer());

        writeId(ServerPacketId.EX_RANKING_CHAR_HISTORY);
        writeInt(history.size());
        for (var data : history) {
            writeInt(data.getDate());
            writeInt(data.getRank());
            writeLong(data.getExp());
        }
    }
}
