package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
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
    public MoveToLocationInVehicle(Player player, Location destination, Location origin) {
        _charObjId = player.getObjectId();
        _boatId = player.getBoat().getObjectId();
        _destination = destination;
        _origin = origin;
    }

    @Override
    public void writeImpl(GameClient client) {
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
