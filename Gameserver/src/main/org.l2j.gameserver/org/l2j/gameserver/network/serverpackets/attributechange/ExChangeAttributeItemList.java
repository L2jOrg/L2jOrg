package org.l2j.gameserver.network.serverpackets.attributechange;

import org.l2j.gameserver.model.ItemInfo;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.AbstractItemPacket;

/**
 * @author Mobius
 */
public class ExChangeAttributeItemList extends AbstractItemPacket {
    private final ItemInfo[] _itemsList;
    private final int _itemId;

    public ExChangeAttributeItemList(int itemId, ItemInfo[] itemsList) {
        _itemId = itemId;
        _itemsList = itemsList;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_CHANGE_ATTRIBUTE_ITEM_LIST);
        writeInt(_itemId);
        writeInt(_itemsList.length);
        for (ItemInfo item : _itemsList) {
            writeItem(item);
        }
    }

}
