package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author KenM
 */
public class ExRedSky extends IClientOutgoingPacket {
    private final int _duration;

    public ExRedSky(int duration) {
        _duration = duration;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_RED_SKY.writeId(packet);

        packet.putInt(_duration);
    }

    @Override
    protected int size(L2GameClient client) {
        return 9;
    }
}
