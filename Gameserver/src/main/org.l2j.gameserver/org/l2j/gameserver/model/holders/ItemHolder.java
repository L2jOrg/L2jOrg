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

import org.l2j.gameserver.model.interfaces.IIdentifiable;

/**
 * A simple DTO for items; contains item ID and count.<br>
 * Extended by {@link ItemChanceHolder}, {@link QuestItemHolder}, {@link UniqueItemHolder}.
 *
 * @author UnAfraid
 */
public class ItemHolder implements IIdentifiable {
    private final int id;
    private final long count;
    private final int enchantment;

    public ItemHolder(int id, long count) {
        this(id, count, 0);
    }

    public ItemHolder(int id, long count, int enchantment) {
        this.id = id;
        this.count = count;
        this.enchantment = enchantment;
    }

    @Override
    public int getId() {
        return id;
    }

    public long getCount() {
        return count;
    }

    public int getEnchantment() {
        return enchantment;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ItemHolder objInstance)) {
            return false;
        } else if (obj == this) {
            return true;
        }
        return (id == objInstance.getId()) && (count == objInstance.getCount());
    }

    @Override
    public String toString() {
        return "[" + getClass().getSimpleName() + "] ID: " + id + ", count: " + count;
    }
}
