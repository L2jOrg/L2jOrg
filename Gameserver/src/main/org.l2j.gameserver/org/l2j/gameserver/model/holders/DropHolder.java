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
package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.enums.DropType;

/**
 * @author Mobius
 */
public class DropHolder {
    private final DropType _dropType;
    private final int _itemId;
    private final long _min;
    private final long _max;
    private final double _chance;

    public DropHolder(DropType dropType, int itemId, long min, long max, double chance) {
        _dropType = dropType;
        _itemId = itemId;
        _min = min;
        _max = max;
        _chance = chance;
    }

    public DropType getDropType() {
        return _dropType;
    }

    public int getItemId() {
        return _itemId;
    }

    public long getMin() {
        return _min;
    }

    public long getMax() {
        return _max;
    }

    public double getChance() {
        return _chance;
    }
}
