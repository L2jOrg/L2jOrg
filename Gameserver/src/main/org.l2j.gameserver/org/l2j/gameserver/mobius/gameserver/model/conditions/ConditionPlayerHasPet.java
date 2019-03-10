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
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Summon;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PetInstance;
import org.l2j.gameserver.mobius.gameserver.model.items.L2Item;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;

import java.util.ArrayList;

/**
 * The Class ConditionPlayerHasPet.
 */
public class ConditionPlayerHasPet extends Condition {
    private final ArrayList<Integer> _controlItemIds;

    /**
     * Instantiates a new condition player has pet.
     *
     * @param itemIds the item ids
     */
    public ConditionPlayerHasPet(ArrayList<Integer> itemIds) {
        if ((itemIds.size() == 1) && (itemIds.get(0) == 0)) {
            _controlItemIds = null;
        } else {
            _controlItemIds = itemIds;
        }
    }

    @Override
    public boolean testImpl(L2Character effector, L2Character effected, Skill skill, L2Item item) {
        final L2Summon pet = effector.getActingPlayer().getPet();
        if ((effector.getActingPlayer() == null) || (pet == null)) {
            return false;
        }

        if (_controlItemIds == null) {
            return true;
        }

        final L2ItemInstance controlItem = ((L2PetInstance) pet).getControlItem();
        return (controlItem != null) && _controlItemIds.contains(controlItem.getId());
    }
}
