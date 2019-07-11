package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ChangeWaitType extends ServerPacket {
    public static final int WT_SITTING = 0;
    public static final int WT_STANDING = 1;
    public static final int WT_START_FAKEDEATH = 2;
    public static final int WT_STOP_FAKEDEATH = 3;
    private final int _charObjId;
    private final int _moveType;
    private final int _x;
    private final int _y;
    private final int _z;

    public ChangeWaitType(Creature character, int newMoveType) {
        _charObjId = character.getObjectId();
        _moveType = newMoveType;

        _x = character.getX();
        _y = character.getY();
        _z = character.getZ();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.CHANGE_WAIT_TYPE);

        writeInt(_charObjId);
        writeInt(_moveType);
        writeInt(_x);
        writeInt(_y);
        writeInt(_z);
    }

}
