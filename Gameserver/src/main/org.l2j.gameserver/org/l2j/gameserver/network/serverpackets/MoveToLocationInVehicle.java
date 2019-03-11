package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Maktakien
 */
public class MoveToLocationInVehicle extends IClientOutgoingPacket {
    private final int _charObjId;
    private final int _boatId;
    private final Location _destination;
    private final Location _origin;

    /**
     * @param player
     * @param destination
     * @param origin
     */
    public MoveToLocationInVehicle(L2PcInstance player, Location destination, Location origin) {
        _charObjId = player.getObjectId();
        _boatId = player.getBoat().getObjectId();
        _destination = destination;
        _origin = origin;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.MOVE_TO_LOCATION_IN_VEHICLE.writeId(packet);

        packet.putInt(_charObjId);
        packet.putInt(_boatId);
        packet.putInt(_destination.getX());
        packet.putInt(_destination.getY());
        packet.putInt(_destination.getZ());
        packet.putInt(_origin.getX());
        packet.putInt(_origin.getY());
        packet.putInt(_origin.getZ());
    }
}
