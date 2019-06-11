package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Maktakien
 */
public class VehicleCheckLocation extends IClientOutgoingPacket {
    private final L2Character _boat;

    public VehicleCheckLocation(L2Character boat) {
        _boat = boat;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.VEHICLE_CHECK_LOCATION);

        writeInt(_boat.getObjectId());
        writeInt(_boat.getX());
        writeInt(_boat.getY());
        writeInt(_boat.getZ());
        writeInt(_boat.getHeading());
    }

}
