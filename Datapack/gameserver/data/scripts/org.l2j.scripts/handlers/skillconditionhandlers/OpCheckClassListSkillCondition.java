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
import org.l2j.gameserver.model.base.ClassId;
import org.w3c.dom.Node;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class OpCheckClassListSkillCondition implements SkillCondition {

	public final Set<ClassId> classIds;
	public final SkillConditionAffectType affectType;

	private OpCheckClassListSkillCondition(Set<ClassId> classIds, SkillConditionAffectType affect) {
		this.classIds = classIds;
		this.affectType = affect;
	}

	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		return switch (affectType) {
			case CASTER -> isPlayer(caster) && classIds.contains(caster.getActingPlayer().getClassId());
			case TARGET -> isPlayer(target) && classIds.contains(target.getActingPlayer().getClassId());
			default ->  false;
		};
	}

	public static final class Factory extends SkillConditionFactory {

		@Override
		public SkillCondition create(Node xmlNode) {
			var affect = parseEnum(xmlNode.getAttributes(), SkillConditionAffectType.class, "affect");
			var listNode = xmlNode.getFirstChild();
			Set<ClassId> classIds = Collections.emptySet();
			if(nonNull(listNode)) {
				classIds = Arrays.stream(listNode.getTextContent().split(" ")).map(ClassId::valueOf).collect(Collectors.toSet());
			}
			return new OpCheckClassListSkillCondition(classIds, affect);
		}

		@Override
		public String conditionName() {
			return "class";
		}
	}
}