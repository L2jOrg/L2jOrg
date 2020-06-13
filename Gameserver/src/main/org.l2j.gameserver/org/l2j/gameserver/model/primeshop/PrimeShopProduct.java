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
package org.l2j.gameserver.model.primeshop;

import org.l2j.gameserver.engine.item.ItemEngine;

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
    private byte maxLevel;
    private byte minBirthday;
    private byte maxBirthday;
    private byte restrictionDay;
    private byte availableCount;
    private List<PrimeShopItem> items;
    private byte vipTier;
    private int silverCoin;
    private boolean isVipGift;

    public PrimeShopProduct(int id, List<PrimeShopItem> items) {
        this.id = id;
        this.items = items;
    }

    public int getId() {
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
        return items.stream().mapToLong(item -> ItemEngine.getInstance().getTemplate(item.getId()).isStackable() ? 1 : item.getCount()).sum();
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

    public byte getMinLevel() {
        return minLevel;
    }

    public byte getMaxLevel() {
        return maxLevel;
    }

    public byte getMinBirthday() {
        return minBirthday;
    }

    public byte getMaxBirthday() {
        return maxBirthday;
    }

    public byte getRestrictionDay() {
        return restrictionDay;
    }

    public byte getAvailableCount() {
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

    public void setMaxLevel(byte maxLevel) {
        this.maxLevel = maxLevel;
    }

    public void setMinBirthday(byte minBirthday) {
        this.minBirthday = minBirthday;
    }

    public void setMaxBirthday(byte maxBirthday) {
        this.maxBirthday = maxBirthday;
    }

    public void setRestrictionDay(byte restrictionDay) {
        this.restrictionDay = restrictionDay;
    }

    public void setAvailableCount(byte availableCount) {
        this.availableCount = availableCount;
    }

    public void setVipTier(byte vipTier) {
        this.vipTier = vipTier;
    }

    public byte getVipTier() {
        return vipTier;
    }

    public void setSilverCoin(int silverCoin) {
        this.silverCoin = silverCoin;
    }

    public int getSilverCoin() {
        return silverCoin;
    }

    public void setVipGift(boolean isVipGift) {
       this.isVipGift = isVipGift;
    }

    public boolean isVipGift() {
        return isVipGift;
    }
}
