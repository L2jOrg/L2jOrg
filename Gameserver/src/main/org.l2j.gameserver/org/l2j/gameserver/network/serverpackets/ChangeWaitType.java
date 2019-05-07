package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ChangeWaitType extends IClientOutgoingPacket {
    public static final int WT_SITTING = 0;
    public static final int WT_STANDING = 1;
    public static final int WT_START_FAKEDEATH = 2;
    public static final int WT_STOP_FAKEDEATH = 3;
    private final int _charObjId;
    private final int _moveType;
    private final int _x;
    private final int _y;
    private final int _z;

    public ChangeWaitType(L2Character character, int newMoveType) {
        _charObjId = character.getObjectId();
        _moveType = newMoveType;

        _x = character.getX();
        _y = character.getY();
        _z = character.getZ();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.CHANGE_WAIT_TYPE.writeId(packet);

        packet.putInt(_charObjId);
        packet.putInt(_moveType);
        packet.putInt(_x);
        packet.putInt(_y);
        packet.putInt(_z);
    }

    @Override
    protected int size(L2GameClient client) {
        return 25;
    }
}
