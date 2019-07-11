package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Maktakien
 */
public class VehicleCheckLocation extends ServerPacket {
    private final Creature _boat;

    public VehicleCheckLocation(Creature boat) {
        _boat = boat;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.VEHICLE_CHECK_LOCATION);

        writeInt(_boat.getObjectId());
        writeInt(_boat.getX());
        writeInt(_boat.getY());
        writeInt(_boat.getZ());
        writeInt(_boat.getHeading());
    }

}
