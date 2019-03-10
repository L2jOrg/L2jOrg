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
package org.l2j.gameserver.mobius.gameserver.model.holders;

/**
 * A DTO for items; contains item ID, count and chance.<br>
 * Complemented by {@link ItemChanceHolder}.
 *
 * @author xban1x
 */
public class QuestItemHolder extends ItemHolder {
    private final int _chance;

    public QuestItemHolder(int id, int chance) {
        this(id, chance, 1);
    }

    public QuestItemHolder(int id, int chance, long count) {
        super(id, count);
        _chance = chance;
    }

    /**
     * Gets the chance.
     *
     * @return the drop chance of the item contained in this object
     */
    public int getChance() {
        return _chance;
    }

    @Override
    public String toString() {
        return "[" + getClass().getSimpleName() + "] ID: " + getId() + ", count: " + getCount() + ", chance: " + _chance;
    }
}
