package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.L2Object;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class MoveToPawn extends IClientOutgoingPacket {
    private final int _charObjId;
    private final int _targetId;
    private final int _distance;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _tx;
    private final int _ty;
    private final int _tz;

    public MoveToPawn(L2Character cha, L2Object target, int distance) {
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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.MOVE_TO_PAWN.writeId(packet);

        packet.putInt(_charObjId);
        packet.putInt(_targetId);
        packet.putInt(_distance);

        packet.putInt(_x);
        packet.putInt(_y);
        packet.putInt(_z);
        packet.putInt(_tx);
        packet.putInt(_ty);
        packet.putInt(_tz);
    }
}
