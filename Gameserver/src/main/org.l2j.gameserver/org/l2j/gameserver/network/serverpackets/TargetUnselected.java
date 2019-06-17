package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class TargetUnselected extends ServerPacket {
    private final int _targetObjId;
    private final int _x;
    private final int _y;
    private final int _z;

    /**
     * @param character
     */
    public TargetUnselected(L2Character character) {
        _targetObjId = character.getObjectId();
        _x = character.getX();
        _y = character.getY();
        _z = character.getZ();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.TARGET_UNSELECTED);

        writeInt(_targetObjId);
        writeInt(_x);
        writeInt(_y);
        writeInt(_z);
        writeInt(0x00); // ??
    }

}
