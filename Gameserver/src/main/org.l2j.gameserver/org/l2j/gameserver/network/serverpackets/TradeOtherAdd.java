package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.TradeItem;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Yme
 */
public final class TradeOtherAdd extends AbstractItemPacket {
    private final int _sendType;
    private final TradeItem _item;

    public TradeOtherAdd(int sendType, TradeItem item) {
        _sendType = sendType;
        _item = item;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.TRADE_OTHER_ADD.writeId(packet);
        packet.put((byte) _sendType);
        if (_sendType == 2) {
            packet.putInt(0x01);
        }
        packet.putInt(0x01);
        writeItem(packet, _item);
    }

    @Override
    protected int size(L2GameClient client) {
        return 120;
    }
}
