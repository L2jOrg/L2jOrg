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

/**
 * @author Mobius
 */
public class RestorationItemHolder {
    private final int _id;
    private final long _count;
    private final int _minEnchant;
    private final int _maxEnchant;

    public RestorationItemHolder(int id, long count, int minEnchant, int maxEnchant) {
        _id = id;
        _count = count;
        _minEnchant = minEnchant;
        _maxEnchant = maxEnchant;
    }

    public int getId() {
        return _id;
    }

    public long getCount() {
        return _count;
    }

    public int getMinEnchant() {
        return _minEnchant;
    }

    public int getMaxEnchant() {
        return _maxEnchant;
    }
}
