package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author UnAfraid
 */
public class ExTeleportToLocationActivate extends ServerPacket {
    private final int _objectId;
    private final Location _loc;

    public ExTeleportToLocationActivate(L2Character character) {
        _objectId = character.getObjectId();
        _loc = character.getLocation();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_TELEPORT_TO_LOCATION_ACTIVATE);

        writeInt(_objectId);
        writeInt(_loc.getX());
        writeInt(_loc.getY());
        writeInt(_loc.getZ());
        writeInt(0); // Unknown (this isn't instanceId)
        writeInt(_loc.getHeading());
        writeInt(0); // Unknown
    }

}