package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Migi
 */
public class ExNoticePostArrived extends IClientOutgoingPacket {
    private static final ExNoticePostArrived STATIC_PACKET_TRUE = new ExNoticePostArrived(true);
    private static final ExNoticePostArrived STATIC_PACKET_FALSE = new ExNoticePostArrived(false);
    private final boolean _showAnim;

    public ExNoticePostArrived(boolean showAnimation) {
        _showAnim = showAnimation;
    }

    public static ExNoticePostArrived valueOf(boolean result) {
        return result ? STATIC_PACKET_TRUE : STATIC_PACKET_FALSE;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_NOTICE_POST_ARRIVED.writeId(packet);

        packet.putInt(_showAnim ? 0x01 : 0x00);
    }
}
