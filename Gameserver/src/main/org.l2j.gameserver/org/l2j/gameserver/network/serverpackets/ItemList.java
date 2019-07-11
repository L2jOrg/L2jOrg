package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.ArrayList;
import java.util.List;

public final class ItemList extends AbstractItemPacket {
    private final int _sendType;
    private final Player _activeChar;
    private final List<L2ItemInstance> _items;

    public ItemList(int sendType, Player activeChar) {
        _sendType = sendType;
        _activeChar = activeChar;
        _items = new ArrayList<>(activeChar.getInventory().getItems(item -> !item.isQuestItem()));
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.ITEM_LIST);
        if (_sendType == 2) {
            writeByte((byte) _sendType);
            writeInt(_items.size());
            writeInt(_items.size());
            for (L2ItemInstance item : _items) {
                writeItem(item);
            }
        } else {
            writeByte((byte) 0x01); // _showWindow ? 0x01 : 0x00
            writeInt(0x00);
            writeInt(_items.size());
        }
        writeInventoryBlock(_activeChar.getInventory());
    }
}
