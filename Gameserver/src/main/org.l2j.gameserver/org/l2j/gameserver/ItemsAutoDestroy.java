/*
 * Copyright © 2019-2021 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.instancemanager.ItemsOnGroundManager;
import org.l2j.gameserver.settings.GeneralSettings;

import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class ItemsAutoDestroy {
    private final List<Item> _items = new LinkedList<>();

    private ItemsAutoDestroy() {
        ThreadPool.scheduleAtFixedRate(this::removeItems, ChronoUnit.MINUTES.getDuration(), ChronoUnit.MINUTES.getDuration());
    }

    public synchronized void addItem(Item item) {
        item.setDropTime(System.currentTimeMillis());
        _items.add(item);
    }

    private synchronized void removeItems() {
        if (_items.isEmpty()) {
            return;
        }

        final long curtime = System.currentTimeMillis();
        final Iterator<Item> itemIterator = _items.iterator();
        while (itemIterator.hasNext()) {
            final Item item = itemIterator.next();
            if ((item.getDropTime() == 0) || (item.getItemLocation() != ItemLocation.VOID)) {
                itemIterator.remove();
            } else {
                long autoDestroyTime;

                if (item.getTemplate().hasExImmediateEffect()) {
                    autoDestroyTime = GeneralSettings.autoDestroyHerbTime();
                } else {
                    if( (autoDestroyTime = GeneralSettings.autoDestroyItemTime()) <= 0) {
                        autoDestroyTime = 3600000;
                    }
                }

                if ((curtime - item.getDropTime()) > autoDestroyTime) {
                    item.decayMe();
                    itemIterator.remove();
                    if (GeneralSettings.saveDroppedItems()) {
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