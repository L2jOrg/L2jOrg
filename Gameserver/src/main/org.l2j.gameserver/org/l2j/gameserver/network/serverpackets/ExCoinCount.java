package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

public class ExCoinCount extends ServerPacket {

    private long coins;

    public ExCoinCount(long coins) {
        this.coins = coins;
    }

    @Override
    protected void writeImpl(GameClient client) throws Exception {
        writeId(ServerExPacketId.EX_BLOODY_COIN_COUNT);
        writeLong(coins);
    }
}
