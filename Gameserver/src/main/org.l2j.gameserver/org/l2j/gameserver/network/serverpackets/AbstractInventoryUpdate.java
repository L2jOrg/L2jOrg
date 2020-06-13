/*
 * Copyright © 2019-2020 L2JOrg
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
package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.ItemInfo;
import org.l2j.gameserver.model.item.instance.Item;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public abstract class AbstractInventoryUpdate extends AbstractItemPacket {
    private final IntMap<ItemInfo> items;

    protected AbstractInventoryUpdate() {
        items = new CHashIntMap<>();
    }

    protected AbstractInventoryUpdate(Item item) {
        this();
        addItem(item);
    }

    protected AbstractInventoryUpdate(List<ItemInfo> items) {
        this.items = streamToMap(items.stream());
    }

    protected AbstractInventoryUpdate(Collection<Item> items) {
        this.items = streamToMap(items.stream().map(ItemInfo::new));
    }

    private static IntMap<ItemInfo> streamToMap(Stream<ItemInfo>stream) {
        return stream.collect(CHashIntMap::new, (map, item) -> map.put(item.getObjectId(), item), IntMap::putAll);
    }

    public final void addItem(Item item) {
        items.put(item.getObjectId(), new ItemInfo(item));
    }

    public final void addNewItem(Item item) {
        items.put(item.getObjectId(), new ItemInfo(item, 1));
    }

    public final void addModifiedItem(Item item) {
        items.put(item.getObjectId(), new ItemInfo(item, 2));
    }

    public final void addRemovedItem(Item item) {
        items.put(item.getObjectId(), new ItemInfo(item, 3));
    }

    public final boolean isEmpty() {
        return items.isEmpty();
    }

    protected final void writeItems() {
        writeByte( 0); // 140
        writeInt(items.size()); // 140
        writeInt(items.size()); // 140
        for (ItemInfo item : items.values()) {
            writeShort(item.getChange()); // Update type : 01-add, 02-modify, 03-remove
            writeItem(item);
        }
    }
}
