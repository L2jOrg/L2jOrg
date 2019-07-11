package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class MoveToPawn extends ServerPacket {
    private final int _charObjId;
    private final int _targetId;
    private final int _distance;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _tx;
    private final int _ty;
    private final int _tz;

    public MoveToPawn(Creature cha, L2Object target, int distance) {
        _charObjId = cha.getObjectId();
        _targetId = target.getObjectId();
        _distance = distance;
        _x = cha.getX();
        _y = cha.getY();
        _z = cha.getZ();
        _tx = target.getX();
        _ty = target.getY();
        _tz = target.getZ();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.MOVE_TO_PAWN);

        writeInt(_charObjId);
        writeInt(_targetId);
        writeInt(_distance);

        writeInt(_x);
        writeInt(_y);
        writeInt(_z);
        writeInt(_tx);
        writeInt(_ty);
        writeInt(_tz);
    }

}
