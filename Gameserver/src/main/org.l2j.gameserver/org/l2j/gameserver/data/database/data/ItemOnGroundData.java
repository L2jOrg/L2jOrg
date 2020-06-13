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
package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.gameserver.model.item.instance.Item;

/**
 * @author JoeAlisson
 */
@Table("itemsonground")
public class ItemOnGroundData {

    @Column("object_id")
    private int objectId;

    @Column("item_id")
    private int itemId;

    private long count;

    @Column("enchant_level")
    private int enchantLevel;

    private int x;
    private int y;
    private int z;

    @Column("drop_time")
    private long dropTime;
    private int equipable;

    public static ItemOnGroundData of(Item item) {
        final var data = new ItemOnGroundData();
        data.objectId = item.getObjectId();
        data.itemId = item.getId();
        data.count = item.getCount();
        data.enchantLevel = item.getEnchantLevel();
        data.x = item.getX();
        data.y = item.getY();
        data.z = item.getZ();
        data.dropTime = item.isProtected() ? -1 : item.getDropTime();
        data.equipable = item.isEquipable() ? 1 : 0;
        return data;
    }

    public int getObjectId() {
        return objectId;
    }

    public int getItemId() {
        return itemId;
    }

    public long getCount() {
        return count;
    }

    public int getEnchantLevel() {
        return enchantLevel;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public long getDropTime() {
        return dropTime;
    }
}
