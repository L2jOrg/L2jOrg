package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Migi
 */
public class ExNoticePostSent extends ServerPacket {
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
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_REPLY_WRITE_POST);

        writeInt(_showAnim ? 0x01 : 0x00);
    }

}
