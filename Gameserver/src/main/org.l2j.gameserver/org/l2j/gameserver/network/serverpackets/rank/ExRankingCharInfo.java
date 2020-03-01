package org.l2j.gameserver.network.serverpackets.rank;

import org.l2j.gameserver.data.database.RankManager;
import org.l2j.gameserver.data.database.data.RankData;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import static java.util.Objects.isNull;
import static org.l2j.commons.util.Util.zeroIfNullOrElse;

/**
 * @author JoeAlisson
 */
public class ExRankingCharInfo extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client) {
        var rank = RankManager.getInstance().getRank(client.getPlayer());

        writeId(ServerPacketId.EX_RANKING_CHAR_INFO);
        if(isNull(rank)) {
            writeInt(0);
            writeInt(0);
            writeInt(0);
            writeInt(0);
        } else {
            writeInt(rank.getRank());
            writeInt(rank.getRankRace());
            writeInt(rank.getRankSnapshot());
            writeInt(rank.getRankRaceSnapshot());
        }
    }
}
