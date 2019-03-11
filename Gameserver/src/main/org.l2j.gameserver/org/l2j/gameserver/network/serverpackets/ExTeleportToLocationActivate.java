package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class ExTeleportToLocationActivate extends IClientOutgoingPacket {
    private final int _objectId;
    private final Location _loc;

    public ExTeleportToLocationActivate(L2Character character) {
        _objectId = character.getObjectId();
        _loc = character.getLocation();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_TELEPORT_TO_LOCATION_ACTIVATE.writeId(packet);

        packet.putInt(_objectId);
        packet.putInt(_loc.getX());
        packet.putInt(_loc.getY());
        packet.putInt(_loc.getZ());
        packet.putInt(0); // Unknown (this isn't instanceId)
        packet.putInt(_loc.getHeading());
        packet.putInt(0); // Unknown
    }
}