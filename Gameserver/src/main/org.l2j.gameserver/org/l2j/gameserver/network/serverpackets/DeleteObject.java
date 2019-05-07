package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class DeleteObject extends IClientOutgoingPacket {
    private final int _objectId;

    public DeleteObject(L2Object obj) {
        _objectId = obj.getObjectId();
    }

    public DeleteObject(int objectId) {
        _objectId = objectId;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.DELETE_OBJECT.writeId(packet);

        packet.putInt(_objectId);
        packet.put((byte) 0x00); // c2
    }

    @Override
    protected int size(L2GameClient client) {
        return 10;
    }
}
