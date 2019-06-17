package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

@StaticPacket
public class TradeOtherDone extends ServerPacket {
    public static final TradeOtherDone STATIC_PACKET = new TradeOtherDone();

    private TradeOtherDone() {
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.TRADE_PRESS_OTHER_OK);
    }

}
