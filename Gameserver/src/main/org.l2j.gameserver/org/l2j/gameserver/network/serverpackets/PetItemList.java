package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Collection;

public class PetItemList extends AbstractItemPacket {
    private final Collection<Item> _items;

    public PetItemList(Collection<Item> items) {
        _items = items;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.PET_ITEM_LIST);

        writeShort((short) _items.size());
        for (Item item : _items) {
            writeItem(item);
        }
    }

}
