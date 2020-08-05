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
import org.l2j.gameserver.enums.ItemLocation;

@Table("items")
public class ItemData {

    @Column("owner_id")
    private int ownerId;

    @Column("object_id")
    private int objectId;

    @Column("item_id")
    private int itemId;
    private long count;

    @Column("enchant_level")
    private int enchantLevel;
    private ItemLocation loc;

    @Column("loc_data")
    private int locData;

    @Column("time_of_use")
    private int timeOfUse;
    private int time;

    public int getOwnerId() {
        return ownerId;
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

    public int getEnchantLevel() { return enchantLevel; }

    public ItemLocation getLoc() {
        return loc;
    }

    public int getLocData() {
        return locData;
    }

    public int getTimeOfUse() {
        return timeOfUse;
    }

    public float getTime() {
        return time;
    }
}