package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Migi
 */
public class ExNoticePostSent extends IClientOutgoingPacket {
    private static final ExNoticePostSent STATIC_PACKET_TRUE = new ExNoticePostSent(true);
    private static final ExNoticePostSent STATIC_PACKET_FALSE = new ExNoticePostSent(false);
    private final boolean _showAnim;

    public ExNoticePostSent(boolean showAnimation) {
        _showAnim = showAnimation;
    }

    public static ExNoticePostSent valueOf(boolean result) {
        return result ? STATIC_PACKET_TRUE : STATIC_PACKET_FALSE;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_REPLY_WRITE_POST.writeId(packet);

        packet.putInt(_showAnim ? 0x01 : 0x00);
    }
}
