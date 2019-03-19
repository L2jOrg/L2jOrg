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
package org.l2j.gameserver.model.items.enchant;

import org.l2j.gameserver.model.holders.RangeChanceHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * @author UnAfraid
 */
public final class EnchantItemGroup {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnchantItemGroup.class);
    private final List<RangeChanceHolder> _chances = new ArrayList<>();
    private final String _name;

    public EnchantItemGroup(String name) {
        _name = name;
    }

    /**
     * @return name of current enchant item group.
     */
    public String getName() {
        return _name;
    }

    /**
     * @param holder
     */
    public void addChance(RangeChanceHolder holder) {
        _chances.add(holder);
    }

    /**
     * @param index
     * @return chance for success rate for current enchant item group.
     */
    public double getChance(int index) {
        if (!_chances.isEmpty()) {
            for (RangeChanceHolder holder : _chances) {
                if ((holder.getMin() <= index) && (holder.getMax() >= index)) {
                    return holder.getChance();
                }
            }
            LOGGER.warn(getClass().getSimpleName() + ": Couldn't match proper chance for item group: " + _name, new IllegalStateException());
            return _chances.get(_chances.size() - 1).getChance();
        }
        LOGGER.warn(": item group: " + _name + " doesn't have any chances!");
        return -1;
    }
}
