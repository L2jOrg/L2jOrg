package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class DeleteObject extends ServerPacket {
    private final int _objectId;

    public DeleteObject(WorldObject obj) {
        _objectId = obj.getObjectId();
    }

    public DeleteObject(int objectId) {
        _objectId = objectId;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.DELETE_OBJECT);

        writeInt(_objectId);
        writeByte((byte) 0x00); // c2
    }

}
