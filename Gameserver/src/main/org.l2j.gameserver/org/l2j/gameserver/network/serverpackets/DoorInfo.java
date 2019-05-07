package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2DoorInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class DoorInfo extends IClientOutgoingPacket {
    private final L2DoorInstance _door;

    public DoorInfo(L2DoorInstance door) {
        _door = door;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.DOOR_INFO.writeId(packet);

        packet.putInt(_door.getObjectId());
        packet.putInt(_door.getId());
    }

    @Override
    protected int size(L2GameClient client) {
        return 13;
    }
}
