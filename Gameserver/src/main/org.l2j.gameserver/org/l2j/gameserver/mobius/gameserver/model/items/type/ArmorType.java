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
package org.l2j.gameserver.mobius.gameserver.model.items.type;

/**
 * Armor Type enumerated.
 */
public enum ArmorType implements ItemType {
    NONE,
    LIGHT,
    HEAVY,
    MAGIC,
    SIGIL,
    RING,
    EARRING,
    NECKLACE,

    // L2J CUSTOM
    SHIELD;

    final int _mask;

    /**
     * Constructor of the ArmorType.
     */
    ArmorType() {
        _mask = 1 << (ordinal() + WeaponType.values().length);
    }

    /**
     * @return the ID of the ArmorType after applying a mask.
     */
    @Override
    public int mask() {
        return _mask;
    }
}
