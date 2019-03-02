package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.templates.item.ItemTemplate;

import java.nio.ByteBuffer;
import java.util.Collection;

public class ExResponseCommissionItemList extends L2GameServerPacket {

    private final int _sendType;
    private final Collection<ItemInstance> _items;

    public ExResponseCommissionItemList(int sendType, Collection<ItemInstance> items)
    {
        _sendType = sendType;
        _items = items;
    }


    @Override
    protected void writeImpl(GameClient client, ByteBuffer buffer) {
        buffer.put((byte) _sendType);
        if (_sendType == 2) {
            buffer.putInt(_items.size());
            buffer.putInt(_items.size());
            for (ItemInstance itemInstance : _items) {
                writeItemInfo(buffer, itemInstance);
            }
        } else {
            buffer.putInt(0);
            buffer.putInt(0);
        }
    }
}