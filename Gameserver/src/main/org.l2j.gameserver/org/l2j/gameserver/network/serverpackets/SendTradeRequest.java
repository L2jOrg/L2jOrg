package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class SendTradeRequest extends IClientOutgoingPacket {
    private final int _senderId;

    public SendTradeRequest(int senderId) {
        _senderId = senderId;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.TRADE_REQUEST);

        writeInt(_senderId);
    }

}
