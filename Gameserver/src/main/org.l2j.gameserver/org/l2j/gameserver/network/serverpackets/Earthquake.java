package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class Earthquake extends IClientOutgoingPacket {
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _intensity;
    private final int _duration;

    /**
     * @param location
     * @param intensity
     * @param duration
     */
    public Earthquake(ILocational location, int intensity, int duration) {
        _x = location.getX();
        _y = location.getY();
        _z = location.getZ();
        _intensity = intensity;
        _duration = duration;
    }

    /**
     * @param x
     * @param y
     * @param z
     * @param intensity
     * @param duration
     */
    public Earthquake(int x, int y, int z, int intensity, int duration) {
        _x = x;
        _y = y;
        _z = z;
        _intensity = intensity;
        _duration = duration;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EARTHQUAKE.writeId(packet);

        packet.putInt(_x);
        packet.putInt(_y);
        packet.putInt(_z);
        packet.putInt(_intensity);
        packet.putInt(_duration);
        packet.putInt(0x00); // Unknown
    }
}
