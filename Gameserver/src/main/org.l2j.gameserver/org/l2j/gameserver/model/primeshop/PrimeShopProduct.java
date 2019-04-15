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
package org.l2j.gameserver.model.primeshop;

import java.util.List;

/**
 * @author UnAfraid
 */
public class PrimeShopProduct {

    private final int id;
    private byte category;
    private byte paymentType;
    private int price;
    private byte panelType;
    private byte recommended;
    private int start;
    private int end;
    private byte daysOfWeek;
    private byte startHour;
    private byte startMinute;
    private byte stopHour;
    private byte stopMinute;
    private byte stock;
    private byte maxStock;
    private byte salePercent;
    private byte minLevel;
    private int maxLevel;
    private int minBirthday;
    private int maxBirthday;
    private int restrictionDay;
    private int availableCount;
    private List<PrimeShopItem> items;

    public PrimeShopProduct(int id, List<PrimeShopItem> items) {
        this.id = id;
        this.items = items;
    }

    public int getBrId() {
        return id;
    }

    public byte getCategory() {
        return category;
    }

    public byte getPaymentType() {
        return paymentType;
    }

    public int getPrice() {
        return price;
    }

    public long getCount() {
        return items.stream().mapToLong(PrimeShopItem::getCount).sum();
    }

    public int getWeight() {
        return items.stream().mapToInt(PrimeShopItem::getWeight).sum();
    }

    public byte getPanelType() {
        return panelType;
    }

    public byte getRecommended() {
        return recommended;
    }

    public int getStartSale() {
        return start;
    }

    public int getEndSale() {
        return end;
    }

    public byte getDaysOfWeek() {
        return daysOfWeek;
    }

    public byte getStartHour() {
        return startHour;
    }

    public byte getStartMinute() {
        return startMinute;
    }

    public byte getStopHour() {
        return stopHour;
    }

    public byte getStopMinute() {
        return stopMinute;
    }

    public int getStock() {
        return stock;
    }

    public byte getTotal() {
        return maxStock;
    }

    public byte getSalePercent() {
        return salePercent;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getMinBirthday() {
        return minBirthday;
    }

    public int getMaxBirthday() {
        return maxBirthday;
    }

    public int getRestrictionDay() {
        return restrictionDay;
    }

    public int getAvailableCount() {
        return availableCount;
    }

    public List<PrimeShopItem> getItems() {
        return items;
    }

    public void setCategory(byte category) {
        this.category = category;
    }

    public void setPaymentType(byte paymentType) {
        this.paymentType = paymentType;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setPanelType(byte panelType) {
        this.panelType = panelType;
    }

    public void setRecommended(byte recommended) {
        this.recommended = recommended;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setDaysOfWeek(byte daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public void setStartHour(byte startHour) {
        this.startHour = startHour;
    }

    public void setStartMinute(byte startMinute) {
        this.startMinute = startMinute;
    }

    public void setStopHour(byte stopHour) {
        this.stopHour = stopHour;
    }

    public void setStopMinute(byte stopMinute) {
        this.stopMinute = stopMinute;
    }

    public void setStock(byte stock) {
        this.stock = stock;
    }

    public void setMaxStock(byte maxStock) {
        this.maxStock = maxStock;
    }

    public void setSalePercent(byte salePercent) {
        this.salePercent = salePercent;
    }

    public void setMinLevel(byte minLevel) {
        this.minLevel = minLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public void setMinBirthday(int minBirthday) {
        this.minBirthday = minBirthday;
    }

    public void setMaxBirthday(int maxBirthday) {
        this.maxBirthday = maxBirthday;
    }

    public void setRestrictionDay(int restrictionDay) {
        this.restrictionDay = restrictionDay;
    }

    public void setAvailableCount(int availableCount) {
        this.availableCount = availableCount;
    }
}
