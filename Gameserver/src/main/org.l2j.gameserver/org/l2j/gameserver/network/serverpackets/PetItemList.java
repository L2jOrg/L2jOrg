package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Collection;

public class PetItemList extends AbstractItemPacket {
    private final Collection<Item> _items;

    public PetItemList(Collection<Item> items) {
        _items = items;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.PET_ITEMLIST);

        writeShort((short) _items.size());
        for (Item item : _items) {
            writeItem(item);
        }
    }

}
