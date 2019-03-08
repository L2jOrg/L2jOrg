package org.l2j.gameserver.mobius.gameserver.network.serverpackets;


import org.l2j.gameserver.mobius.gameserver.model.Location;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

/**
 * @author UnAfraid
 */
public class ExTeleportToLocationActivate implements IClientOutgoingPacket
{
    private final int _objectId;
    private final Location _loc;

    public ExTeleportToLocationActivate(L2Character character)
    {
        _objectId = character.getObjectId();
        _loc = character.getLocation();
    }

    @Override
    public boolean write(PacketWriter packet)
    {
        OutgoingPackets.EX_TELEPORT_TO_LOCATION_ACTIVATE.writeId(packet);

        packet.writeD(_objectId);
        packet.writeD(_loc.getX());
        packet.writeD(_loc.getY());
        packet.writeD(_loc.getZ());
        packet.writeD(0); // Unknown (this isn't instanceId)
        packet.writeD(_loc.getHeading());
        packet.writeD(0); // Unknown
        return true;
    }
}