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
package org.l2j.gameserver.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bit
 */
public class FishingBaitData {
    private final int _itemId;
    private final int _level;
    private final int _minPlayerLevel;
    private final double _chance;
    private final int _timeMin;
    private final int _timeMax;
    private final int _waitMin;
    private final int _waitMax;
    private final List<Integer> _rewards = new ArrayList<>();

    public FishingBaitData(int itemId, int level, int minPlayerLevel, double chance, int timeMin, int timeMax, int waitMin, int waitMax) {
        _itemId = itemId;
        _level = level;
        _minPlayerLevel = minPlayerLevel;
        _chance = chance;
        _timeMin = timeMin;
        _timeMax = timeMax;
        _waitMin = waitMin;
        _waitMax = waitMax;
    }

    public int getItemId() {
        return _itemId;
    }

    public int getLevel() {
        return _level;
    }

    public int getMinPlayerLevel() {
        return _minPlayerLevel;
    }

    public double getChance() {
        return _chance;
    }

    public int getTimeMin() {
        return _timeMin;
    }

    public int getTimeMax() {
        return _timeMax;
    }

    public int getWaitMin() {
        return _waitMin;
    }

    public int getWaitMax() {
        return _waitMax;
    }

    public List<Integer> getRewards() {
        return _rewards;
    }

    public void addReward(int itemId) {
        _rewards.add(itemId);
    }
}
