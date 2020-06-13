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
package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.enums.SkillConditionAffectType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.w3c.dom.Node;

import static org.l2j.commons.util.Util.isBetween;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class CheckLevelSkillCondition implements SkillCondition {
	public final int minLevel;
	public final int maxLevel;
	public final SkillConditionAffectType affectType;

	private CheckLevelSkillCondition(SkillConditionAffectType affect, int minLevel, int maxLevel) {
		this.affectType = affect;
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
	}

	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		return switch (affectType) {
			case CASTER -> isBetween(caster.getLevel(), minLevel, maxLevel);
			case TARGET -> isPlayer(target) && isBetween(target.getActingPlayer().getLevel(), minLevel, maxLevel);
			default -> false;
		};
	}

	public static final class Factory extends SkillConditionFactory {

		@Override
		public SkillCondition create(Node xmlNode) {
			var attr = xmlNode.getAttributes();
			return new CheckLevelSkillCondition(parseEnum(attr, SkillConditionAffectType.class, "affect"), parseInt(attr, "min-level"), parseInt(attr, "max-level"));
		}

		@Override
		public String conditionName() {
			return "level";
		}
	}
}
