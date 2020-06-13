/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.model.commission;

import org.l2j.gameserver.model.ItemInfo;
import org.l2j.gameserver.model.item.instance.Item;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ScheduledFuture;

/**
 * @author NosBit
 */
public class CommissionItem {
    private final long _commissionId;
    private final Item _itemInstance;
    private final ItemInfo _itemInfo;
    private final long _pricePerUnit;
    private final Instant _startTime;
    private final byte _durationInDays;
    private ScheduledFuture<?> _saleEndTask;

    public CommissionItem(long commissionId, Item itemInstance, long pricePerUnit, Instant startTime, byte durationInDays) {
        _commissionId = commissionId;
        _itemInstance = itemInstance;
        _itemInfo = new ItemInfo(_itemInstance);
        _pricePerUnit = pricePerUnit;
        _startTime = startTime;
        _durationInDays = durationInDays;
    }

    /**
     * Gets the commission id.
     *
     * @return the commission id
     */
    public long getCommissionId() {
        return _commissionId;
    }

    /**
     * Gets the item instance.
     *
     * @return the item instance
     */
    public Item getItemInstance() {
        return _itemInstance;
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
        return _pricePerUnit;
    }

    /**
     * Gets the start time.
     *
     * @return the start time
     */
    public Instant getStartTime() {
        return _startTime;
    }

    /**
     * Gets the duration in days.
     *
     * @return the duration in days
     */
    public byte getDurationInDays() {
        return _durationInDays;
    }

    /**
     * Gets the end time.
     *
     * @return the end time
     */
    public Instant getEndTime() {
        return _startTime.plus(_durationInDays, ChronoUnit.DAYS);
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
