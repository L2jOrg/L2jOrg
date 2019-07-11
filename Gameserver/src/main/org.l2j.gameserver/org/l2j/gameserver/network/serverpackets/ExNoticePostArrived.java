package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Migi
 */
public class ExNoticePostArrived extends ServerPacket {
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
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_NOTICE_POST_ARRIVED);

        writeInt(_showAnim ? 0x01 : 0x00);
    }

}
