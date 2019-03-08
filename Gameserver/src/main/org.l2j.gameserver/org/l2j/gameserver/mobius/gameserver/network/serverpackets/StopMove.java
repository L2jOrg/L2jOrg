package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

public final class StopMove implements IClientOutgoingPacket
{
    private final int _objectId;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _heading;

    public StopMove(L2Character cha)
    {
        this(cha.getObjectId(), cha.getX(), cha.getY(), cha.getZ(), cha.getHeading());
    }

    /**
     * @param objectId
     * @param x
     * @param y
     * @param z
     * @param heading
     */
    public StopMove(int objectId, int x, int y, int z, int heading)
    {
        _objectId = objectId;
        _x = x;
        _y = y;
        _z = z;
        _heading = heading;
    }

    @Override
    public boolean write(PacketWriter packet)
    {
        OutgoingPackets.STOP_MOVE.writeId(packet);

        packet.writeD(_objectId);
        packet.writeD(_x);
        packet.writeD(_y);
        packet.writeD(_z);
        packet.writeD(_heading);
        return true;
    }
}
