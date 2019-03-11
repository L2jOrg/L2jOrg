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
package org.l2j.gameserver.model;

/**
 * @author JIV
 */
public class L2ExtractableProduct {
    private final int _id;
    private final int _min;
    private final int _max;
    private final int _chance;
    private final int _minEnchant;
    private final int _maxEnchant;

    /**
     * Create Extractable product
     *
     * @param id         create item id
     * @param min        item count max
     * @param max        item count min
     * @param chance     chance for creating
     * @param minEnchant item min enchant
     * @param maxEnchant item max enchant
     */
    public L2ExtractableProduct(int id, int min, int max, double chance, int minEnchant, int maxEnchant) {
        _id = id;
        _min = min;
        _max = max;
        _chance = (int) (chance * 1000);
        _minEnchant = minEnchant;
        _maxEnchant = maxEnchant;
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

    public int getChance() {
        return _chance;
    }

    public int getMinEnchant() {
        return _minEnchant;
    }

    public int getMaxEnchant() {
        return _maxEnchant;
    }
}
