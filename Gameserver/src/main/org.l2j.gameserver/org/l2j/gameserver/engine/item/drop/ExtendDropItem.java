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
package org.l2j.gameserver.engine.item.drop;

import org.l2j.gameserver.model.holders.ItemHolder;

import java.util.Objects;

/**
 * @author Sdw
 */
public class ExtendDropItem extends ItemHolder {
    private final long maxCount;
    private final float chance;

    public ExtendDropItem(int id, long count, long maxCount, float chance) {
        super(id, count);

        this.maxCount = maxCount;
        this.chance = chance;
    }

    public long getMaxCount() {
        return maxCount;
    }

    public double getChance() {
        return chance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ExtendDropItem that = (ExtendDropItem) o;
        return maxCount == that.maxCount && Float.compare(that.chance, chance) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxCount, chance);
    }
}