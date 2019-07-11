package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class TargetUnselected extends ServerPacket {
    private final int _targetObjId;
    private final int _x;
    private final int _y;
    private final int _z;

    /**
     * @param character
     */
    public TargetUnselected(Creature character) {
        _targetObjId = character.getObjectId();
        _x = character.getX();
        _y = character.getY();
        _z = character.getZ();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.TARGET_UNSELECTED);

        writeInt(_targetObjId);
        writeInt(_x);
        writeInt(_y);
        writeInt(_z);
        writeInt(0x00); // ??
    }

}
