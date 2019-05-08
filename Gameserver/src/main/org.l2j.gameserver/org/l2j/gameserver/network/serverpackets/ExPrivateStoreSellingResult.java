package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ExPrivateStoreSellingResult extends IClientOutgoingPacket {
    private final int _objectId;
    private final long _count;
    private final String _buyer;

    public ExPrivateStoreSellingResult(int objectId, long count, String buyer) {
        _objectId = objectId;
        _count = count;
        _buyer = buyer;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_PRIVATE_STORE_SELLING_RESULT.writeId(packet);
        packet.putInt(_objectId);
        packet.putLong(_count);
        writeString(_buyer, packet);
    }

    @Override
    protected int size(L2GameClient client) {
        return 20 + _buyer.length() * 2;
    }
}