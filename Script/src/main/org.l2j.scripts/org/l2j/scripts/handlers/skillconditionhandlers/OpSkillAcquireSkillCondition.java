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

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.w3c.dom.Node;

import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class OpSkillAcquireSkillCondition implements SkillCondition {

	public final int skillId;
	public final boolean hasLearned;

	private OpSkillAcquireSkillCondition(int skill, boolean learned) {
		this.skillId = skill;
		this.hasLearned = learned;
	}

	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		if (!isCreature(target)) {
			return false;
		}
		final int skillLevel = ((Creature) target).getSkillLevel(skillId);
		return hasLearned == (skillLevel != 0);
	}


	public static final class Factory extends SkillConditionFactory {

		@Override
		public SkillCondition create(Node xmlNode) {
			var attr = xmlNode.getAttributes();
			return new OpSkillAcquireSkillCondition(parseInt(attr, "id"), parseBoolean(attr, "learned"));
		}

		@Override
		public String conditionName() {
			return "skill";
		}
	}
}
