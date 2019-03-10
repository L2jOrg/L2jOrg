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
import org.l2j.gameserver.mobius.gameserver.model.items.L2Item;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;

import java.util.List;

/**
 * The Class ConditionPlayerServitorNpcId.
 */
public class ConditionPlayerServitorNpcId extends Condition {
    private final List<Integer> _npcIds;

    /**
     * Instantiates a new condition player servitor npc id.
     *
     * @param npcIds the npc ids
     */
    public ConditionPlayerServitorNpcId(List<Integer> npcIds) {
        if ((npcIds.size() == 1) && (npcIds.get(0) == 0)) {
            _npcIds = null;
        } else {
            _npcIds = npcIds;
        }
    }

    @Override
    public boolean testImpl(L2Character effector, L2Character effected, Skill skill, L2Item item) {
        if ((effector.getActingPlayer() == null) || !effector.getActingPlayer().hasSummon()) {
            return false;
        }
        if (_npcIds == null) {
            return true;
        }
        for (L2Summon summon : effector.getServitors().values()) {
            if (_npcIds.contains(summon.getId())) {
                return true;
            }
        }
        return false;
    }
}
