package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class SendTradeRequest extends IClientOutgoingPacket {
    private final int _senderId;

    public SendTradeRequest(int senderId) {
        _senderId = senderId;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.TRADE_REQUEST.writeId(packet);

        packet.putInt(_senderId);
    }
}
