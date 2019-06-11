package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Maktakien
 */
public class StopMoveInVehicle extends IClientOutgoingPacket {
    private final int _charObjId;
    private final int _boatId;
    private final Location _pos;
    private final int _heading;

    public StopMoveInVehicle(L2PcInstance player, int boatId) {
        _charObjId = player.getObjectId();
        _boatId = boatId;
        _pos = player.getInVehiclePosition();
        _heading = player.getHeading();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.STOP_MOVE_IN_VEHICLE);

        writeInt(_charObjId);
        writeInt(_boatId);
        writeInt(_pos.getX());
        writeInt(_pos.getY());
        writeInt(_pos.getZ());
        writeInt(_heading);
    }

}
