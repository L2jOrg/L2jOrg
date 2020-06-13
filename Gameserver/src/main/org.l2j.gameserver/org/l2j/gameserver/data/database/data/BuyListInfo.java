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

/**
 * @author JoeAlisson
 */
@Table("buylists")
public class BuyListInfo {

    @Column("buylist_id")
    private int id;

    @Column("item_id")
    private int itemId;

    private long count;

    @Column("next_restock_time")
    private long nextRestock;

    public static BuyListInfo of(int itemId, int buyListId) {
        final var info = new BuyListInfo();
        info.id = buyListId;
        info.itemId = itemId;
        return info;
    }

    public int getId() {
        return id;
    }

    public int getItemId() {
        return itemId;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getNextRestock() {
        return nextRestock;
    }

    public void setNextRestock(long nextRestock) {
        this.nextRestock = nextRestock;
    }
}
