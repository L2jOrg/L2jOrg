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
package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.model.StatsSet;

/**
 * @author Sdw
 */
public class ItemPointHolder extends ItemHolder {
    private final int _points;

    public ItemPointHolder(StatsSet params) {
        this(params.getInt("id"), params.getLong("count"), params.getInt("points"));
    }

    public ItemPointHolder(int id, long count, int points) {
        super(id, count);
        _points = points;
    }

    /**
     * Gets the point.
     *
     * @return the number of point to get the item
     */
    public int getPoints() {
        return _points;
    }

    @Override
    public String toString() {
        return "[" + getClass().getSimpleName() + "] ID: " + getId() + ", count: " + getCount() + ", points: " + _points;
    }
}
