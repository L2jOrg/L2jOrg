package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ExMoveToLocationInAirShip extends ServerPacket {
    private final int _charObjId;
    private final int _airShipId;
    private final Location _destination;
    private final int _heading;

    /**
     * @param player
     */
    public ExMoveToLocationInAirShip(Player player) {
        _charObjId = player.getObjectId();
        _airShipId = player.getAirShip().getObjectId();
        _destination = player.getInVehiclePosition();
        _heading = player.getHeading();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_MOVE_TO_LOCATION_IN_AIR_SHIP);

        writeInt(_charObjId);
        writeInt(_airShipId);
        writeInt(_destination.getX());
        writeInt(_destination.getY());
        writeInt(_destination.getZ());
        writeInt(_heading);
    }

}
