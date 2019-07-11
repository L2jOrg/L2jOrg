package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ExPrivateStoreBuyingResult extends ServerPacket {
    private final int _objectId;
    private final long _count;
    private final String _seller;

    public ExPrivateStoreBuyingResult(int objectId, long count, String seller) {
        _objectId = objectId;
        _count = count;
        _seller = seller;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_PRIVATE_STORE_BUYING_RESULT);
        writeInt(_objectId);
        writeLong(_count);
        writeString(_seller);
    }

}