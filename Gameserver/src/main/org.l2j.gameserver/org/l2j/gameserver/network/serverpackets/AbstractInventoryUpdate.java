package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.ItemInfo;
import org.l2j.gameserver.model.items.instance.Item;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author UnAfraid
 */
public abstract class AbstractInventoryUpdate extends AbstractItemPacket {
    private final Map<Integer, ItemInfo> _items = new ConcurrentSkipListMap<>();

    public AbstractInventoryUpdate() {
    }

    public AbstractInventoryUpdate(Item item) {
        addItem(item);
    }

    public AbstractInventoryUpdate(List<ItemInfo> items) {
        for (ItemInfo item : items) {
            _items.put(item.getObjectId(), item);
        }
    }

    public final void addItem(Item item) {
        _items.put(item.getObjectId(), new ItemInfo(item));
    }

    public final void addNewItem(Item item) {
        _items.put(item.getObjectId(), new ItemInfo(item, 1));
    }

    public final void addModifiedItem(Item item) {
        _items.put(item.getObjectId(), new ItemInfo(item, 2));
    }

    public final void addRemovedItem(Item item) {
        _items.put(item.getObjectId(), new ItemInfo(item, 3));
    }

    public final void addItems(List<Item> items) {
        for (Item item : items) {
            _items.put(item.getObjectId(), new ItemInfo(item));
        }
    }

    public final Collection<ItemInfo> getItems() {
        return _items.values();
    }

    protected final void writeItems() {
        writeByte((byte) 0); // 140
        writeInt(0); // 140
        writeInt(_items.size()); // 140
        for (ItemInfo item : _items.values()) {
            writeShort((short) item.getChange()); // Update type : 01-add, 02-modify, 03-remove
            writeItem(item);
        }
    }

}
