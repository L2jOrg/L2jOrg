package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.L2Object;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

public final class TeleportToLocation implements IClientOutgoingPacket
{
    private final int _targetObjId;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _heading;

    public TeleportToLocation(L2Object obj, int x, int y, int z, int heading)
    {
        _targetObjId = obj.getObjectId();
        _x = x;
        _y = y;
        _z = z;
        _heading = heading;
    }

    @Override
    public boolean write(PacketWriter packet)
    {
        OutgoingPackets.TELEPORT_TO_LOCATION.writeId(packet);

        packet.writeD(_targetObjId);
        packet.writeD(_x);
        packet.writeD(_y);
        packet.writeD(_z);
        packet.writeD(0x00); // isValidation ??
        packet.writeD(_heading);
        packet.writeD(0x00); // Unknown
        return true;
    }
}
