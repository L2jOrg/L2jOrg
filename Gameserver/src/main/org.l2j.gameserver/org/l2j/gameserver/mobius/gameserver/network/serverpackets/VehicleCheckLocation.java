package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.VEHICLE_CHECK_LOCATION.writeId(packet);

        packet.putInt(_boat.getObjectId());
        packet.putInt(_boat.getX());
        packet.putInt(_boat.getY());
        packet.putInt(_boat.getZ());
        packet.putInt(_boat.getHeading());
    }
}
