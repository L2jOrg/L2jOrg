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

import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.enums.Position;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.w3c.dom.Node;

import static org.l2j.gameserver.util.MathUtil.convertHeadingToDegree;

/**
 * @author Sdw
 */
public class OpBlinkSkillCondition implements SkillCondition {
	private final int angle;
	private final int range;

	private OpBlinkSkillCondition(Position direction, int range) {
		angle = switch (direction) {
			case BACK -> 0;
			case FRONT -> 180;
			default -> -1;
		};
		this.range = range;

	}

	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {

		final double angle = convertHeadingToDegree(caster.getHeading());
		final double radian = Math.toRadians(angle);
		final double course = Math.toRadians(this.angle);
		final int x1 = (int) (Math.cos(Math.PI + radian + course) * range);
		final int y1 = (int) (Math.sin(Math.PI + radian + course) * range);
		
		final int x = caster.getX() + x1;
		final int y = caster.getY() + y1;
		final int z = caster.getZ();
		
		return GeoEngine.getInstance().canMoveToTarget(caster.getX(), caster.getY(), caster.getZ(), x, y, z, caster.getInstanceWorld());
	}

	public static final class Factory extends SkillConditionFactory {

		@Override
		public SkillCondition create(Node xmlNode) {
			var attr = xmlNode.getAttributes();
			return new OpBlinkSkillCondition(parseEnum(attr, Position.class, "direction"), parseInt(attr, "range"));
		}

		@Override
		public String conditionName() {
			return "blink";
		}
	}
}
