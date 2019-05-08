package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Maktakien
 */
public class GetOffVehicle extends IClientOutgoingPacket {
    private final int _charObjId;
    private final int _boatObjId;
    private final int _x;
    private final int _y;
    private final int _z;

    /**
     * @param charObjId
     * @param boatObjId
     * @param x
     * @param y
     * @param z
     */
    public GetOffVehicle(int charObjId, int boatObjId, int x, int y, int z) {
        _charObjId = charObjId;
        _boatObjId = boatObjId;
        _x = x;
        _y = y;
        _z = z;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.GET_OFF_VEHICLE.writeId(packet);

        packet.putInt(_charObjId);
        packet.putInt(_boatObjId);
        packet.putInt(_x);
        packet.putInt(_y);
        packet.putInt(_z);
    }

    @Override
    protected int size(L2GameClient client) {
        return 25;
    }
}
