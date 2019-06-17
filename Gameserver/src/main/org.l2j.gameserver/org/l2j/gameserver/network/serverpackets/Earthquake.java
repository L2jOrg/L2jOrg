package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class Earthquake extends ServerPacket {
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
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EARTHQUAKE);

        writeInt(_x);
        writeInt(_y);
        writeInt(_z);
        writeInt(_intensity);
        writeInt(_duration);
        writeInt(0x00); // Unknown
    }

}
