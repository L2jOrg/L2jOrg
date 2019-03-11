package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class TradeDone extends IClientOutgoingPacket {
    private final int _num;

    public TradeDone(int num) {
        _num = num;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.TRADE_DONE.writeId(packet);

        packet.putInt(_num);
    }
}
