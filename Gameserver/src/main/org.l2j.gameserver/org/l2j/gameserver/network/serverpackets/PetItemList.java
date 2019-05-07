package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Collection;

public class PetItemList extends AbstractItemPacket {
    private final Collection<L2ItemInstance> _items;

    public PetItemList(Collection<L2ItemInstance> items) {
        _items = items;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PET_ITEM_LIST.writeId(packet);

        packet.putShort((short) _items.size());
        for (L2ItemInstance item : _items) {
            writeItem(packet, item);
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 6 +_items.size() * 100;
    }
}
