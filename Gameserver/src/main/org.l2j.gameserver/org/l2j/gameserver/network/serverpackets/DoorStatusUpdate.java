package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2DoorInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class DoorStatusUpdate extends IClientOutgoingPacket {
    private final L2DoorInstance _door;

    public DoorStatusUpdate(L2DoorInstance door) {
        _door = door;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.DOOR_STATUS_UPDATE.writeId(packet);

        packet.putInt(_door.getObjectId());
        packet.putInt(_door.isOpen() ? 0 : 1);
        packet.putInt(_door.getDamage());
        packet.putInt(_door.isEnemy() ? 1 : 0);
        packet.putInt(_door.getId());
        packet.putInt((int) _door.getCurrentHp());
        packet.putInt(_door.getMaxHp());
    }
}