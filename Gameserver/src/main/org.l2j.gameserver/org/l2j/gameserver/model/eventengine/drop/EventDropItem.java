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
package org.l2j.gameserver.model.eventengine.drop;

/**
 * @author UnAfraid
 */
public class EventDropItem {
    private final int _id;
    private final int _min;
    private final int _max;
    private final double _chance;

    public EventDropItem(int id, int min, int max, double chance) {
        _id = id;
        _min = min;
        _max = max;
        _chance = chance;
    }

    public int getId() {
        return _id;
    }

    public int getMin() {
        return _min;
    }

    public int getMax() {
        return _max;
    }

    public double getChance() {
        return _chance;
    }
}
