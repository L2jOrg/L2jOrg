package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class RadarControl extends IClientOutgoingPacket {
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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.RADAR_CONTROL.writeId(packet);

        packet.putInt(_showRadar);
        packet.putInt(_type); // maybe type
        packet.putInt(_x);
        packet.putInt(_y);
        packet.putInt(_z);
    }

    @Override
    protected int size(L2GameClient client) {
        return 25;
    }
}
