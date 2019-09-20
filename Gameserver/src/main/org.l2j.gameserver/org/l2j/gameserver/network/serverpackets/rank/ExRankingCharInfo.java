package org.l2j.gameserver.network.serverpackets.rank;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExRankingCharInfo extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_RANKING_CHAR_INFO);
        writeInt(1); // server rank
        writeInt(2); // race rank
        writeInt(2); // server rank snapshot
        writeInt(1); // race rank snapshot
    }
}
