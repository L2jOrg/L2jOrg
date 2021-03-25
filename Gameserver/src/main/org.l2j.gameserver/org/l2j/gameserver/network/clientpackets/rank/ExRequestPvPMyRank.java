package org.l2j.gameserver.network.clientpackets.rank;

import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.rank.ExPvPRankingMyInfo;

public class ExRequestPvPMyRank extends ClientPacket {

    @Override
    protected void readImpl() throws Exception {
        // dummy byte
    }

    @Override
    protected void runImpl() {
        client.sendPacket(new ExPvPRankingMyInfo(client.getPlayer()));
    }
}
