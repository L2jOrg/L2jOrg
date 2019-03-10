/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.model.primeshop;

import org.l2j.gameserver.mobius.gameserver.model.StatsSet;

import java.util.List;

/**
 * @author UnAfraid
 */
public class PrimeShopGroup {
    private final int _brId;
    private final int _category;
    private final int _paymentType;
    private final int _price;
    private final int _panelType;
    private final int _recommended;
    private final int _start;
    private final int _end;
    private final int _daysOfWeek;
    private final int _startHour;
    private final int _startMinute;
    private final int _stopHour;
    private final int _stopMinute;
    private final int _stock;
    private final int _maxStock;
    private final int _salePercent;
    private final int _minLevel;
    private final int _maxLevel;
    private final int _minBirthday;
    private final int _maxBirthday;
    private final int _restrictionDay;
    private final int _availableCount;
    private final List<PrimeShopItem> _items;

    public PrimeShopGroup(StatsSet set, List<PrimeShopItem> items) {
        _brId = set.getInt("id");
        _category = set.getInt("cat", 0);
        _paymentType = set.getInt("paymentType", 0);
        _price = set.getInt("price");
        _panelType = set.getInt("panelType", 0);
        _recommended = set.getInt("recommended", 0);
        _start = set.getInt("startSale", 0);
        _end = set.getInt("endSale", 0);
        _daysOfWeek = set.getInt("daysOfWeek", 127);
        _startHour = set.getInt("startHour", 0);
        _startMinute = set.getInt("startMinute", 0);
        _stopHour = set.getInt("stopHour", 0);
        _stopMinute = set.getInt("stopMinute", 0);
        _stock = set.getInt("stock", 0);
        _maxStock = set.getInt("maxStock", -1);
        _salePercent = set.getInt("salePercent", 0);
        _minLevel = set.getInt("minLevel", 0);
        _maxLevel = set.getInt("maxLevel", 0);
        _minBirthday = set.getInt("minBirthday", 0);
        _maxBirthday = set.getInt("maxBirthday", 0);
        _restrictionDay = set.getInt("restrictionDay", 0);
        _availableCount = set.getInt("availableCount", 0);
        _items = items;
    }

    public int getBrId() {
        return _brId;
    }

    public int getCat() {
        return _category;
    }

    public int getPaymentType() {
        return _paymentType;
    }

    public int getPrice() {
        return _price;
    }

    public long getCount() {
        return _items.stream().mapToLong(PrimeShopItem::getCount).sum();
    }

    public int getWeight() {
        return _items.stream().mapToInt(PrimeShopItem::getWeight).sum();
    }

    public int getPanelType() {
        return _panelType;
    }

    public int getRecommended() {
        return _recommended;
    }

    public int getStartSale() {
        return _start;
    }

    public int getEndSale() {
        return _end;
    }

    public int getDaysOfWeek() {
        return _daysOfWeek;
    }

    public int getStartHour() {
        return _startHour;
    }

    public int getStartMinute() {
        return _startMinute;
    }

    public int getStopHour() {
        return _stopHour;
    }

    public int getStopMinute() {
        return _stopMinute;
    }

    public int getStock() {
        return _stock;
    }

    public int getTotal() {
        return _maxStock;
    }

    public int getSalePercent() {
        return _salePercent;
    }

    public int getMinLevel() {
        return _minLevel;
    }

    public int getMaxLevel() {
        return _maxLevel;
    }

    public int getMinBirthday() {
        return _minBirthday;
    }

    public int getMaxBirthday() {
        return _maxBirthday;
    }

    public int getRestrictionDay() {
        return _restrictionDay;
    }

    public int getAvailableCount() {
        return _availableCount;
    }

    public List<PrimeShopItem> getItems() {
        return _items;
    }
}
