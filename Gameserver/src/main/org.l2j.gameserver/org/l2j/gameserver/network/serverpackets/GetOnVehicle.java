package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

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
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.GET_ON_VEHICLE);

        writeInt(_charObjId);
        writeInt(_boatObjId);
        writeInt(_pos.getX());
        writeInt(_pos.getY());
        writeInt(_pos.getZ());
    }

}
