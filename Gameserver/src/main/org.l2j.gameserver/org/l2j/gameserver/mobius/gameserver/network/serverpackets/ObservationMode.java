package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.Location;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

public class ObservationMode implements IClientOutgoingPacket
{
    private final Location _loc;

    public ObservationMode(Location loc)
    {
        _loc = loc;
    }

    @Override
    public boolean write(PacketWriter packet)
    {
        OutgoingPackets.OBSERVER_START.writeId(packet);

        packet.writeD(_loc.getX());
        packet.writeD(_loc.getY());
        packet.writeD(_loc.getZ());
        packet.writeD(0x00); // TODO: Find me
        packet.writeD(0xc0); // TODO: Find me
        return true;
    }
}
