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
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.item.type.ArmorType;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * The Class ConditionUsingItemType.
 *
 * @author mkizub
 */
public final class ConditionUsingItemType extends Condition {
    public final boolean _armor;
    public final int _mask;

    /**
     * Instantiates a new condition using item type.
     *
     * @param mask the mask
     */
    public ConditionUsingItemType(int mask) {
        _mask = mask;
        _armor = (_mask & (ArmorType.MAGIC.mask() | ArmorType.LIGHT.mask() | ArmorType.HEAVY.mask())) != 0;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        if (isNull(effector)) {
            return false;
        }

        if (!isPlayer(effector)) {
            return !_armor && (_mask & effector.getAttackType().mask()) != 0;
        }

        final Inventory inv = effector.getInventory();
        // If ConditionUsingItemType is one between Light, Heavy or Magic
        if (_armor) {
            // Get the itemMask of the weared chest (if exists)
            final Item chest = inv.getPaperdollItem(InventorySlot.CHEST);
            if (chest == null) {
                return (ArmorType.NONE.mask() & _mask) == ArmorType.NONE.mask();
            }
            final int chestMask = chest.getTemplate().getItemMask();

            // If chest armor is different from the condition one return false
            if ((_mask & chestMask) == 0) {
                return false;
            }

            // So from here, chest armor matches conditions
            var chestBodyPart = chest.getBodyPart();
            // return True if chest armor is a Full Armor
            if (chestBodyPart == BodyPart.FULL_ARMOR) {
                return true;
            }
            // check legs armor
            final Item legs = inv.getPaperdollItem(InventorySlot.LEGS);
            if (legs == null) {
                return (ArmorType.NONE.mask() & _mask) == ArmorType.NONE.mask();
            }
            final int legMask = legs.getTemplate().getItemMask();
            // return true if legs armor matches too
            return (_mask & legMask) != 0;
        }
        return (_mask & inv.getWearedMask()) != 0;
    }
}
