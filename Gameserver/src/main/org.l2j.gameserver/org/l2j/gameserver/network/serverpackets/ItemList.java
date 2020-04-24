package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.ArrayList;
import java.util.List;

public final class ItemList extends AbstractItemPacket {
    private final int _sendType;
    private final Player _activeChar;
    private final List<Item> _items;

    public ItemList(int sendType, Player activeChar) {
        _sendType = sendType;
        _activeChar = activeChar;
        _items = new ArrayList<>(activeChar.getInventory().getItems(item -> !item.isQuestItem()));
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.ITEMLIST);
        writeByte(_sendType);
        if(_sendType == 1) {
            writeInt(0x00); // special item count
            writeInt(_items.size());
        }
        if (_sendType == 2) {
            writeInt(_items.size()); // total items
            writeInt(_items.size()); // items on page
            for (Item item : _items) {
                writeItem(item, client.getPlayer());
            }
        }
        writeInventoryBlock(_activeChar.getInventory());
    }
}
