package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class DoorInfo extends ServerPacket {
    private final Door _door;

    public DoorInfo(Door door) {
        _door = door;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.DOOR_INFO);

        writeInt(_door.getObjectId());
        writeInt(_door.getId());
    }

}
