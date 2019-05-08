package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * @author Migi, DS
 */
public class ExReplyPostItemList extends AbstractItemPacket {
    private final int _sendType;
    private final L2PcInstance _activeChar;
    private final Collection<L2ItemInstance> _itemList;

    public ExReplyPostItemList(int sendType, L2PcInstance activeChar) {
        _sendType = sendType;
        _activeChar = activeChar;
        _itemList = _activeChar.getInventory().getAvailableItems(true, false, false);
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_REPLY_POST_ITEM_LIST.writeId(packet);
        packet.put((byte) _sendType);
        packet.putInt(_itemList.size());
        if (_sendType == 2) {
            packet.putInt(_itemList.size());
            for (L2ItemInstance item : _itemList) {
                writeItem(packet, item);
            }
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 17 + _itemList.size() * 100;
    }
}
