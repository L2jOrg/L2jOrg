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

import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.enums.ResidenceType;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.w3c.dom.Node;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class OpHomeSkillCondition implements SkillCondition {

	public final ResidenceType type;

	private OpHomeSkillCondition(ResidenceType type) {
		this.type = type;
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		if (isPlayer(caster)) {
			final Clan clan = caster.getActingPlayer().getClan();
			if (nonNull(clan)) {
				return switch (type) {
					case CASTLE -> nonNull(CastleManager.getInstance().getCastleByOwner(clan));
					case CLANHALL -> nonNull(ClanHallManager.getInstance().getClanHallByClan(clan));
					default -> false;
				};
			}
		}
		return false;
	}

	public static final class Factory extends SkillConditionFactory {

		@Override
		public SkillCondition create(Node xmlNode) {
			return new OpHomeSkillCondition(parseEnum(xmlNode.getAttributes(), ResidenceType.class, "Type"));
		}

		@Override
		public String conditionName() {
			return "residence";
		}
	}
}
