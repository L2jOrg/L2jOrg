package org.l2j.gameserver.network.serverpackets.rank;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.data.database.data.RankData;
import org.l2j.gameserver.engine.rank.RankEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;


public class ExPvPRankingMyInfo extends ServerPacket {
    private final RankData rank;


    public ExPvPRankingMyInfo(Player player) {
        rank = RankEngine.getInstance().getRank(player);
    }
    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_PVP_RANKING_MY_INFO, buffer );

            buffer.writeInt(5); //Todo pvppoint
            buffer.writeInt(1); //Todo rank
            buffer.writeInt(5); //Todo prevrank
            buffer.writeInt(5); //Todo killcount
            buffer.writeInt(5); //Todo diecount

    }
}
