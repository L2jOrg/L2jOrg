/*
 * Copyright © 2019 L2J Mobius
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

import org.l2j.commons.util.Util;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.world.World;

import static org.l2j.commons.util.Util.isNullOrEmpty;

/**
 * Condition which checks if you are within the given range of a summoned by you npc.
 *
 * @author Nik
 */
public class ConditionPlayerRangeFromSummonedNpc extends Condition {
    /**
     * NPC Ids.
     */
    private final int[] npcIds;
    /**
     * Radius to check.
     */
    private final int radius;
    /**
     * Expected value.
     */
    private final boolean _val;

    public ConditionPlayerRangeFromSummonedNpc(int[] npcIds, int radius, boolean val) {
        this.npcIds = npcIds;
        this.radius = radius;
        _val = val;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        boolean existNpc = false;
        if(!isNullOrEmpty(npcIds) && radius > 0) {
            existNpc = World.getInstance().hasAnyVisibleObjectInRange(effector, Npc.class, radius, npc -> Util.contains(npcIds, npc.getId()) && effector == npc.getSummoner());
        }
        return existNpc == _val;
    }
}
