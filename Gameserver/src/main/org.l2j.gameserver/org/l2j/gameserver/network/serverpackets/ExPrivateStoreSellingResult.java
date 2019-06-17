package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ExPrivateStoreSellingResult extends ServerPacket {
    private final int _objectId;
    private final long _count;
    private final String _buyer;

    public ExPrivateStoreSellingResult(int objectId, long count, String buyer) {
        _objectId = objectId;
        _count = count;
        _buyer = buyer;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_PRIVATE_STORE_SELLING_RESULT);
        writeInt(_objectId);
        writeLong(_count);
        writeString(_buyer);
    }

}