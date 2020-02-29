package org.l2j.gameserver.network.serverpackets.rank;

import org.l2j.gameserver.data.database.RankManager;
import org.l2j.gameserver.data.database.data.RankData;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import static org.l2j.commons.util.Util.zeroIfNullOrElse;

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
        writeInt(zeroIfNullOrElse(rank, RankData::getRank)); // server rank
        writeInt(zeroIfNullOrElse(rank, RankData::getRankRace)); // race rank
        writeInt(zeroIfNullOrElse(snapshot, RankData::getRank)); // server rank snapshot
        writeInt(zeroIfNullOrElse(snapshot, RankData::getRankRace)); // race rank snapshot
    }
}
