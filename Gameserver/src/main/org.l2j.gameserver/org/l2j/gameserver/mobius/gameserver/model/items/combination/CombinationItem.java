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
package org.l2j.gameserver.mobius.gameserver.model.items.combination;

import org.l2j.gameserver.mobius.gameserver.model.StatsSet;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author UnAfraid
 */
public class CombinationItem {
    private final int _itemOne;
    private final int _itemTwo;
    private final int _chance;
    private final Map<CombinationItemType, CombinationItemReward> _rewards = new EnumMap<>(CombinationItemType.class);

    public CombinationItem(StatsSet set) {
        _itemOne = set.getInt("one");
        _itemTwo = set.getInt("two");
        _chance = set.getInt("chance");
    }

    public int getItemOne() {
        return _itemOne;
    }

    public int getItemTwo() {
        return _itemTwo;
    }

    public int getChance() {
        return _chance;
    }

    public void addReward(CombinationItemReward item) {
        _rewards.put(item.getType(), item);
    }

    public CombinationItemReward getReward(CombinationItemType type) {
        return _rewards.get(type);
    }
}
