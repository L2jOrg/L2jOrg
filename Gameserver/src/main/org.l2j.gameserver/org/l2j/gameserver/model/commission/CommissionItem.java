/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.model.commission;

import org.l2j.gameserver.data.database.data.CommissionItemData;
import org.l2j.gameserver.data.database.data.ItemData;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.model.ItemInfo;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ScheduledFuture;

/**
 * @author NosBit
 */
public class CommissionItem {
    private final Item itemInstance;
    private final ItemInfo _itemInfo;
    private final CommissionItemData data;
    private ScheduledFuture<?> _saleEndTask;

    public CommissionItem(CommissionItemData data, ItemData itemData) {
        this(data, new Item(itemData));

    }

    public CommissionItem(CommissionItemData data, Item itemInstance) {
        this.itemInstance = itemInstance;
        this.data = data;
        _itemInfo = new ItemInfo(itemInstance);
    }

    /**
     * Gets the commission id.
     *
     * @return the commission id
     */
    public long getCommissionId() {
        return data.getCommissionId();
    }

    /**
     * Gets the item instance.
     *
     * @return the item instance
     */
    public Item getItemInstance() {
        return itemInstance;
    }

    /**
     * Gets the item info.
     *
     * @return the item info
     */
    public ItemInfo getItemInfo() {
        return _itemInfo;
    }

    /**
     * Gets the price per unit.
     *
     * @return the price per unit
     */
    public long getPricePerUnit() {
        return data.getPrice();
    }

    /**
     * Gets the duration in days.
     *
     * @return the duration in days
     */
    public byte getDurationInDays() {
        return data.getDuration();
    }

    /**
     * Gets the end time.
     *
     * @return the end time
     */
    public Instant getEndTime() {
        return data.getStartTime().plus(data.getDuration(), ChronoUnit.DAYS);
    }

    /**
     * Gets the sale end task.
     *
     * @return the sale end task
     */
    public ScheduledFuture<?> getSaleEndTask() {
        return _saleEndTask;
    }

    /**
     * Sets the sale end task.
     *
     * @param saleEndTask the sale end task
     */
    public void setSaleEndTask(ScheduledFuture<?> saleEndTask) {
        _saleEndTask = saleEndTask;
    }
}
