package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2BoatInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Maktakien
 */
public class VehicleInfo extends IClientOutgoingPacket {
    private final int _objId;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _heading;

    public VehicleInfo(L2BoatInstance boat) {
        _objId = boat.getObjectId();
        _x = boat.getX();
        _y = boat.getY();
        _z = boat.getZ();
        _heading = boat.getHeading();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.VEHICLE_INFO.writeId(packet);

        packet.putInt(_objId);
        packet.putInt(_x);
        packet.putInt(_y);
        packet.putInt(_z);
        packet.putInt(_heading);
    }
}
