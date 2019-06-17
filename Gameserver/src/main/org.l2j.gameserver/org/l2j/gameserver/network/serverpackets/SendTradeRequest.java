package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class SendTradeRequest extends ServerPacket {
    private final int _senderId;

    public SendTradeRequest(int senderId) {
        _senderId = senderId;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.TRADE_REQUEST);

        writeInt(_senderId);
    }

}
