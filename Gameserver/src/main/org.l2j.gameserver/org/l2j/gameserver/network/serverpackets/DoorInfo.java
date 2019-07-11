package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class DoorInfo extends ServerPacket {
    private final Door _door;

    public DoorInfo(Door door) {
        _door = door;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.DOOR_INFO);

        writeInt(_door.getObjectId());
        writeInt(_door.getId());
    }

}
