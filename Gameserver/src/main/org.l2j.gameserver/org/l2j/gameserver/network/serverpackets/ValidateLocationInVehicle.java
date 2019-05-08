package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ValidateLocationInVehicle extends IClientOutgoingPacket {
    private final int _charObjId;
    private final int _boatObjId;
    private final int _heading;
    private final Location _pos;

    public ValidateLocationInVehicle(L2PcInstance player) {
        _charObjId = player.getObjectId();
        _boatObjId = player.getBoat().getObjectId();
        _heading = player.getHeading();
        _pos = player.getInVehiclePosition();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.VALIDATE_LOCATION_IN_VEHICLE.writeId(packet);

        packet.putInt(_charObjId);
        packet.putInt(_boatObjId);
        packet.putInt(_pos.getX());
        packet.putInt(_pos.getY());
        packet.putInt(_pos.getZ());
        packet.putInt(_heading);
    }

    @Override
    protected int size(L2GameClient client) {
        return 29;
    }
}
