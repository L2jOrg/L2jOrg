package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2DoorInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class DoorStatusUpdate extends ServerPacket {
    private final L2DoorInstance _door;

    public DoorStatusUpdate(L2DoorInstance door) {
        _door = door;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.DOOR_STATUS_UPDATE);

        writeInt(_door.getObjectId());
        writeInt(_door.isOpen() ? 0 : 1);
        writeInt(_door.getDamage());
        writeInt(_door.isEnemy() ? 1 : 0);
        writeInt(_door.getId());
        writeInt((int) _door.getCurrentHp());
        writeInt(_door.getMaxHp());
    }

}