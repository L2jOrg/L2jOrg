package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class SetupGauge extends IClientOutgoingPacket {
    public static final int BLUE = 0;
    public static final int RED = 1;
    public static final int CYAN = 2;

    private final int _dat1;
    private final int _time;
    private final int _time2;
    private final int _charObjId;

    public SetupGauge(int objectId, int dat1, int time) {
        _charObjId = objectId;
        _dat1 = dat1; // color 0-blue 1-red 2-cyan 3-green
        _time = time;
        _time2 = time;
    }

    public SetupGauge(int objectId, int color, int currentTime, int maxTime) {
        _charObjId = objectId;
        _dat1 = color; // color 0-blue 1-red 2-cyan 3-green
        _time = currentTime;
        _time2 = maxTime;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.SETUP_GAUGE.writeId(packet);
        packet.putInt(_charObjId);
        packet.putInt(_dat1);
        packet.putInt(_time);
        packet.putInt(_time2);
    }
}
