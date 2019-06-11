package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.TradeItem;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Yme
 */
public final class TradeOwnAdd extends AbstractItemPacket {
    private final int _sendType;
    private final TradeItem _item;

    public TradeOwnAdd(int sendType, TradeItem item) {
        _sendType = sendType;
        _item = item;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.TRADE_OWN_ADD);
        writeByte((byte) _sendType);
        if (_sendType == 2) {
            writeInt(0x01);
        }
        writeInt(0x01);
        writeItem(_item);
    }

}
