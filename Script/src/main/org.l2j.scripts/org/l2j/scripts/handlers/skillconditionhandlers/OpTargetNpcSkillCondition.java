/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.scripts.handlers.skillconditionhandlers;

import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;

import static org.l2j.gameserver.util.GameUtils.isNpc;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class OpTargetNpcSkillCondition implements SkillCondition {

    public final IntSet npcId;

    protected OpTargetNpcSkillCondition(IntSet npcs) {
        npcId = npcs;
    }

    @Override
    public boolean canUse(Creature caster, Skill skill, WorldObject target)
    {
        return isNpc(target) && npcId.contains(target.getId());
    }
}
