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
package org.l2j.gameserver.mobius.gameserver.model.conditions;

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.items.L2Item;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;

/**
 * The Class ConditionSlotItemId.
 *
 * @author mkizub
 */
public final class ConditionSlotItemId extends ConditionInventory {
    private final int _itemId;
    private final int _enchantLevel;

    /**
     * Instantiates a new condition slot item id.
     *
     * @param slot         the slot
     * @param itemId       the item id
     * @param enchantLevel the enchant level
     */
    public ConditionSlotItemId(int slot, int itemId, int enchantLevel) {
        super(slot);
        _itemId = itemId;
        _enchantLevel = enchantLevel;
    }

    @Override
    public boolean testImpl(L2Character effector, L2Character effected, Skill skill, L2Item item) {
        if ((effector == null) || !effector.isPlayer()) {
            return false;
        }

        final L2ItemInstance itemSlot = effector.getInventory().getPaperdollItem(_slot);
        if (itemSlot == null) {
            return _itemId == 0;
        }
        return (itemSlot.getId() == _itemId) && (itemSlot.getEnchantLevel() >= _enchantLevel);
    }
}
