package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ExMoveToLocationInAirShip extends IClientOutgoingPacket {
    private final int _charObjId;
    private final int _airShipId;
    private final Location _destination;
    private final int _heading;

    /**
     * @param player
     */
    public ExMoveToLocationInAirShip(L2PcInstance player) {
        _charObjId = player.getObjectId();
        _airShipId = player.getAirShip().getObjectId();
        _destination = player.getInVehiclePosition();
        _heading = player.getHeading();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_MOVE_TO_LOCATION_IN_AIR_SHIP.writeId(packet);

        packet.putInt(_charObjId);
        packet.putInt(_airShipId);
        packet.putInt(_destination.getX());
        packet.putInt(_destination.getY());
        packet.putInt(_destination.getZ());
        packet.putInt(_heading);
    }

    @Override
    protected int size(L2GameClient client) {
        return 29;
    }
}
