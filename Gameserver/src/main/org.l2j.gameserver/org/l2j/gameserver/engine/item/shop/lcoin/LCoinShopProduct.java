/*
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
package org.l2j.gameserver.engine.item.shop.lcoin;

import org.l2j.gameserver.engine.item.shop.l2store.RestrictionPeriod;
import org.l2j.gameserver.model.holders.ItemHolder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public record LCoinShopProduct(int id, int restrictionAmount, RestrictionPeriod restrictionPeriod, int minLevel, int minClanLevel, List<ItemHolder> ingredients, List<ItemHolder> productions, int remainServerItemAmount, LocalDateTime expiration, int chance) {
    public int getRemainAmount() {
        return restrictionAmount;
    }

    public int remainTime() {
        return nonNull(expiration) ? (int) Math.max(0, Duration.between(LocalDateTime.now(), expiration).toSeconds()) : 0;
    }

    public boolean isExpired() {
        return nonNull(expiration) && LocalDateTime.now().isAfter(expiration);
    }
}
