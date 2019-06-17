package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.TradeItem;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author daemon
 */
public class TradeUpdate extends AbstractItemPacket {
    private final int _sendType;
    private final TradeItem _item;
    private final long _newCount;
    private final long _count;

    public TradeUpdate(int sendType, L2PcInstance player, TradeItem item, long count) {
        _sendType = sendType;
        _count = count;
        _item = item;
        _newCount = player == null ? 0 : player.getInventory().getItemByObjectId(item.getObjectId()).getCount() - item.getCount();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.TRADE_UPDATE);
        writeByte((byte) _sendType);
        writeInt(0x01);
        if (_sendType == 2) {
            writeInt(0x01);
            writeShort((short) ((_newCount > 0) && _item.getItem().isStackable() ? 3 : 2));
            writeItem(_item, _count);
        }
    }

}
