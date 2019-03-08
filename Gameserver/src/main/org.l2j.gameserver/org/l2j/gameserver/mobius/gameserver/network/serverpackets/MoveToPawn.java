package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.L2Object;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

public class MoveToPawn implements IClientOutgoingPacket
{
    private final int _charObjId;
    private final int _targetId;
    private final int _distance;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _tx;
    private final int _ty;
    private final int _tz;

    public MoveToPawn(L2Character cha, L2Object target, int distance)
    {
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
    public boolean write(PacketWriter packet)
    {
        OutgoingPackets.MOVE_TO_PAWN.writeId(packet);

        packet.writeD(_charObjId);
        packet.writeD(_targetId);
        packet.writeD(_distance);

        packet.writeD(_x);
        packet.writeD(_y);
        packet.writeD(_z);
        packet.writeD(_tx);
        packet.writeD(_ty);
        packet.writeD(_tz);
        return true;
    }
}
