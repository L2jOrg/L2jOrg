package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class Revive extends ServerPacket {
    private final int _objectId;

    public Revive(WorldObject obj) {
        _objectId = obj.getObjectId();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.REVIVE);

        writeInt(_objectId);
    }

}
