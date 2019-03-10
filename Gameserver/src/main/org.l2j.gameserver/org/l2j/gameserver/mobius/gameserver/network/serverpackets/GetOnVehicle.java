package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.Location;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Maktakien
 */
public class GetOnVehicle extends IClientOutgoingPacket {
    private final int _charObjId;
    private final int _boatObjId;
    private final Location _pos;

    /**
     * @param charObjId
     * @param boatObjId
     * @param pos
     */
    public GetOnVehicle(int charObjId, int boatObjId, Location pos) {
        _charObjId = charObjId;
        _boatObjId = boatObjId;
        _pos = pos;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.GET_ON_VEHICLE.writeId(packet);

        packet.putInt(_charObjId);
        packet.putInt(_boatObjId);
        packet.putInt(_pos.getX());
        packet.putInt(_pos.getY());
        packet.putInt(_pos.getZ());
    }
}
