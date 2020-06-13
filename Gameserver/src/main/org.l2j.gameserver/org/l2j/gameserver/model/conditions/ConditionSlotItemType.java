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
package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.item.instance.Item;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * The Class ConditionSlotItemType.
 *
 * @author mkizub
 */
public final class ConditionSlotItemType extends ConditionInventory {
    private final int _mask;

    /**
     * Instantiates a new condition slot item type.
     *
     * @param slot the slot
     * @param mask the mask
     */
    public ConditionSlotItemType(int slot, int mask) {
        super(slot);
        _mask = mask;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        if (!isPlayer(effector)) {
            return false;
        }

        final Item itemSlot = effector.getInventory().getPaperdollItem(InventorySlot.fromId(_slot));
        if (itemSlot == null) {
            return false;
        }
        return (itemSlot.getTemplate().getItemMask() & _mask) != 0;
    }
}
