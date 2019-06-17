package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class RadarControl extends ServerPacket {
    private final int _showRadar;
    private final int _type;
    private final int _x;
    private final int _y;
    private final int _z;

    public RadarControl(int showRadar, int type, int x, int y, int z) {
        _showRadar = showRadar; // showRader?? 0 = showradar; 1 = delete radar;
        _type = type; // radar type??
        _x = x;
        _y = y;
        _z = z;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.RADAR_CONTROL);

        writeInt(_showRadar);
        writeInt(_type); // maybe type
        writeInt(_x);
        writeInt(_y);
        writeInt(_z);
    }

}
