package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ExPrivateStoreBuyingResult extends IClientOutgoingPacket {
    private final int _objectId;
    private final long _count;
    private final String _seller;

    public ExPrivateStoreBuyingResult(int objectId, long count, String seller) {
        _objectId = objectId;
        _count = count;
        _seller = seller;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_PRIVATE_STORE_BUYING_RESULT);
        writeInt(_objectId);
        writeLong(_count);
        writeString(_seller);
    }

}