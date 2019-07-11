package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Boat;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Maktakien
 */
public class VehicleDeparture extends ServerPacket {
    private final int _objId;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _moveSpeed;
    private final int _rotationSpeed;

    public VehicleDeparture(Boat boat) {
        _objId = boat.getObjectId();
        _x = boat.getXdestination();
        _y = boat.getYdestination();
        _z = boat.getZdestination();
        _moveSpeed = (int) boat.getMoveSpeed();
        _rotationSpeed = (int) boat.getStat().getRotationSpeed();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.VEHICLE_DEPARTURE);

        writeInt(_objId);
        writeInt(_moveSpeed);
        writeInt(_rotationSpeed);
        writeInt(_x);
        writeInt(_y);
        writeInt(_z);
    }

}
