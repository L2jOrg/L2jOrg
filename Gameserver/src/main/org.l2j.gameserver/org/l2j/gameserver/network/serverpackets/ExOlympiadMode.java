package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author godson
 */
public class ExOlympiadMode extends IClientOutgoingPacket {
    private final int _mode;

    /**
     * @param mode (0 = return, 3 = spectate)
     */
    public ExOlympiadMode(int mode) {
        _mode = mode;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_OLYMPIAD_MODE.writeId(packet);

        packet.put((byte) _mode);
    }

    @Override
    protected int size(L2GameClient client) {
        return 6;
    }
}
