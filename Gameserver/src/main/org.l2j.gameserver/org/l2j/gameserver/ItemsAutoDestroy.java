package org.l2j.gameserver;

import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.instancemanager.ItemsOnGroundManager;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class ItemsAutoDestroy {
    private final List<L2ItemInstance> _items = new LinkedList<>();

    private ItemsAutoDestroy() {
        ThreadPoolManager.scheduleAtFixedRate(this::removeItems, 5000, 5000);
    }

    public synchronized void addItem(L2ItemInstance item) {
        item.setDropTime(System.currentTimeMillis());
        _items.add(item);
    }

    private synchronized void removeItems() {
        if (_items.isEmpty()) {
            return;
        }

        final long curtime = System.currentTimeMillis();
        final Iterator<L2ItemInstance> itemIterator = _items.iterator();
        while (itemIterator.hasNext()) {
            final L2ItemInstance item = itemIterator.next();
            if ((item.getDropTime() == 0) || (item.getItemLocation() != ItemLocation.VOID)) {
                itemIterator.remove();
            } else {
                final long autoDestroyTime;
                if (item.getItem().getAutoDestroyTime() > 0) {
                    autoDestroyTime = item.getItem().getAutoDestroyTime();
                } else if (item.getItem().hasExImmediateEffect()) {
                    autoDestroyTime = Config.HERB_AUTO_DESTROY_TIME;
                } else {
                    autoDestroyTime = ((Config.AUTODESTROY_ITEM_AFTER == 0) ? 3600000 : Config.AUTODESTROY_ITEM_AFTER * 1000);
                }

                if ((curtime - item.getDropTime()) > autoDestroyTime) {
                    item.decayMe();
                    itemIterator.remove();
                    if (Config.SAVE_DROPPED_ITEM) {
                        ItemsOnGroundManager.getInstance().removeObject(item);
                    }
                }
            }
        }
    }

    public static ItemsAutoDestroy getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ItemsAutoDestroy INSTANCE = new ItemsAutoDestroy();
    }
}