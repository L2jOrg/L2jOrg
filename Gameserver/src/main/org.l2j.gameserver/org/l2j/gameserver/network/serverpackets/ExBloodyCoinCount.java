package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

public class ExBloodyCoinCount extends ServerPacket {
    @Override
    protected void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_BLOODY_COIN_COUNT);
        writeLong(client.getPlayer().getL2Coins());
    }
}
