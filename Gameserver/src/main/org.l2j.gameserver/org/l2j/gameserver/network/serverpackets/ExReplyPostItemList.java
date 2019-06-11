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
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_REPLY_POST_ITEM_LIST);
        writeByte((byte) _sendType);
        writeInt(_itemList.size());
        if (_sendType == 2) {
            writeInt(_itemList.size());
            for (L2ItemInstance item : _itemList) {
                writeItem(item);
            }
        }
    }

}
