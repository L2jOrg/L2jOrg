package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

@StaticPacket
public class TradeOtherDone extends ServerPacket {
    public static final TradeOtherDone STATIC_PACKET = new TradeOtherDone();

    private TradeOtherDone() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.TRADE_PRESS_OTHER_OK);
    }

}
