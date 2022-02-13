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

import org.l2j.gameserver.engine.siege.SiegeZone;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Artefact;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.MathUtil;
import org.w3c.dom.Node;

import static java.lang.Math.abs;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class PossessHolythingSkillCondition implements SkillCondition {

	private PossessHolythingSkillCondition() {
		// singleton
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		if(!(caster instanceof Player player) || !checkPlayerConditions(player)) {
			return false;
		}

		return checkSiegeConditions(skill, target, player);
	}

	private boolean checkPlayerConditions(Player player) {
		return !player.isAlikeDead() && player.isClanLeader();
	}

	private boolean checkSiegeConditions(Skill skill, WorldObject target, Player player) {
		var castle = CastleManager.getInstance().getCastle(player);
		var siegeZone = castle.getSiegeZone();

		if (!checkArtifact(target, siegeZone)) {
			player.sendPacket(SystemMessageId.INVALID_TARGET);
			return false;
		}

		return checkSiegeAttacker(skill, target, player, siegeZone);
	}

	private boolean checkSiegeAttacker(Skill skill, WorldObject target, Player player, SiegeZone siegeZone) {
		if (!siegeZone.isAttacker(player)) {
			player.sendPacket(getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skill));
			return false;
		}
		return true;
	}

	private boolean checkArtifact(WorldObject target, SiegeZone siegeZone) {
		if(!(target instanceof Artefact artefact)) {
			return false;
		}

		return siegeZone != null && siegeZone.hasHolyArtifact(artefact);
	}

	public static final class Factory extends SkillConditionFactory {
		private static final PossessHolythingSkillCondition INSTANCE = new PossessHolythingSkillCondition();

		@Override
		public SkillCondition create(Node xmlNode) {
			return INSTANCE;
		}

		@Override
		public String conditionName() {
			return "PossessHolything";
		}
	}
}
