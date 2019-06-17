package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Maktakien
 */
public class MoveToLocationInVehicle extends ServerPacket {
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
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.MOVE_TO_LOCATION_IN_VEHICLE);

        writeInt(_charObjId);
        writeInt(_boatId);
        writeInt(_destination.getX());
        writeInt(_destination.getY());
        writeInt(_destination.getZ());
        writeInt(_origin.getX());
        writeInt(_origin.getY());
        writeInt(_origin.getZ());
    }

}
