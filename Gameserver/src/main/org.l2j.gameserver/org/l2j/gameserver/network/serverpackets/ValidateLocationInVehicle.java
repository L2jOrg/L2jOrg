package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ValidateLocationInVehicle extends ServerPacket {
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
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.VALIDATE_LOCATION_IN_VEHICLE);

        writeInt(_charObjId);
        writeInt(_boatObjId);
        writeInt(_pos.getX());
        writeInt(_pos.getY());
        writeInt(_pos.getZ());
        writeInt(_heading);
    }

}
