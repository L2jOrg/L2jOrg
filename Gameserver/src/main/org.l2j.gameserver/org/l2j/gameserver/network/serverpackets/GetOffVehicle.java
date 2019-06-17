package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Maktakien
 */
public class GetOffVehicle extends ServerPacket {
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
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.GET_OFF_VEHICLE);

        writeInt(_charObjId);
        writeInt(_boatObjId);
        writeInt(_x);
        writeInt(_y);
        writeInt(_z);
    }

}
