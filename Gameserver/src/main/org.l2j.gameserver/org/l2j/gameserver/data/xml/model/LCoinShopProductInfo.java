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
package org.l2j.gameserver.data.xml.model;

import org.l2j.gameserver.model.holders.ItemHolder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Objects.nonNull;

public class LCoinShopProductInfo {

    private final LocalDateTime expiration;

    private final int id;
    private final Category category;
    private final int limitPerDay;
    private final int minLevel;
    private final boolean isEvent;
    private final List<ItemHolder> ingredients;
    private final ItemHolder production;
    private final int remainServerItemAmount;

    public LCoinShopProductInfo(int id, Category category, int limitPerDay, int minLevel, boolean isEvent, List<ItemHolder> ingredients, ItemHolder production, int remainServerItemAmount, LocalDateTime expirationDate) {
        this.id = id;
        this.category = category;
        this.limitPerDay = limitPerDay;
        this.minLevel = minLevel;
        this.isEvent = isEvent;
        this.ingredients = ingredients;
        this.production = production;
        this.remainServerItemAmount = remainServerItemAmount;
        this.expiration = expirationDate;
    }

    public int getId() {
        return id;
    }

    public int getRemainAmount() {
        return limitPerDay; // TODO
    }

    public int getRemainTime() {
        return nonNull(expiration) ? (int) Math.max(0, Duration.between(LocalDateTime.now(), expiration).toSeconds()) : 0;
    }

    public boolean isExpired() {
        return nonNull(expiration) && LocalDateTime.now().isAfter(expiration);
    }

    public int getRemainServerItemAmount() {
        return remainServerItemAmount; // TODO
    }

    public Category getCategory() {
        return category;
    }

    public int getLimitPerDay() {
        return limitPerDay;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public boolean isEvent() {
        return isEvent;
    }

    public List<ItemHolder> getIngredients() {
        return ingredients;
    }

    public ItemHolder getProduction() {
        return production;
    }

    public enum Category {
        Equip,
        Special,
        Supplies,
        Other
    }
}
