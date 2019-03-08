package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

public final class MoveToLocation implements IClientOutgoingPacket
{
    private final int _charObjId;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _xDst;
    private final int _yDst;
    private final int _zDst;

    public MoveToLocation(L2Character cha)
    {
        _charObjId = cha.getObjectId();
        _x = cha.getX();
        _y = cha.getY();
        _z = cha.getZ();
        _xDst = cha.getXdestination();
        _yDst = cha.getYdestination();
        _zDst = cha.getZdestination();
    }

    @Override
    public boolean write(PacketWriter packet)
    {
        OutgoingPackets.MOVE_TO_LOCATION.writeId(packet);

        packet.writeD(_charObjId);

        packet.writeD(_xDst);
        packet.writeD(_yDst);
        packet.writeD(_zDst);

        packet.writeD(_x);
        packet.writeD(_y);
        packet.writeD(_z);
        return true;
    }
}
