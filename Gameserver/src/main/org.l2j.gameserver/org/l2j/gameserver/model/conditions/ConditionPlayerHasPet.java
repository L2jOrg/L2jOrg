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
package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

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
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        final Summon pet = effector.getActingPlayer().getPet();
        if ((effector.getActingPlayer() == null) || (pet == null)) {
            return false;
        }

        if (_controlItemIds == null) {
            return true;
        }

        final Item controlItem = ((Pet) pet).getControlItem();
        return (controlItem != null) && _controlItemIds.contains(controlItem.getId());
    }
}
