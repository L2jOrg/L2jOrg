package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Collection;

/**
 * @author Migi, DS
 */
public class ExReplyPostItemList extends AbstractItemPacket {
    private final int _sendType;
    private final Player _activeChar;
    private final Collection<Item> _itemList;

    public ExReplyPostItemList(int sendType, Player activeChar) {
        _sendType = sendType;
        _activeChar = activeChar;
        _itemList = _activeChar.getInventory().getAvailableItems(true, false, false);
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_REPLY_POST_ITEM_LIST);
        writeByte((byte) _sendType);
        writeInt(_itemList.size());
        if (_sendType == 2) {
            writeInt(_itemList.size());
            for (Item item : _itemList) {
                writeItem(item);
            }
        }
    }

}
