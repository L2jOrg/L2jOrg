package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2BoatInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Maktakien
 */
public class VehicleDeparture extends IClientOutgoingPacket {
    private final int _objId;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _moveSpeed;
    private final int _rotationSpeed;

    public VehicleDeparture(L2BoatInstance boat) {
        _objId = boat.getObjectId();
        _x = boat.getXdestination();
        _y = boat.getYdestination();
        _z = boat.getZdestination();
        _moveSpeed = (int) boat.getMoveSpeed();
        _rotationSpeed = (int) boat.getStat().getRotationSpeed();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.VEHICLE_DEPARTURE.writeId(packet);

        packet.putInt(_objId);
        packet.putInt(_moveSpeed);
        packet.putInt(_rotationSpeed);
        packet.putInt(_x);
        packet.putInt(_y);
        packet.putInt(_z);
    }

    @Override
    protected int size(L2GameClient client) {
        return 31;
    }
}
