package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Kerberos
 */
@Deprecated
public class GMHide extends IClientOutgoingPacket {
    private final int _mode;

    /**
     * @param mode (0 = display windows, 1 = hide windows)
     */
    public GMHide(int mode) {
        _mode = mode;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.GM_HIDE.writeId(packet);

        packet.putInt(_mode);
    }

    @Override
    protected int size(L2GameClient client) {
        return 9;
    }
}
