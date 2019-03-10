package org.l2j.gameserver.mobius.gameserver.network.serverpackets.attributechange;

import org.l2j.gameserver.mobius.gameserver.model.ItemInfo;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.AbstractItemPacket;

import java.nio.ByteBuffer;

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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_CHANGE_ATTRIBUTE_ITEM_LIST.writeId(packet);
        packet.putInt(_itemId);
        packet.putInt(_itemsList.length);
        for (ItemInfo item : _itemsList) {
            writeItem(packet, item);
        }
    }
}
