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
package org.l2j.scripts.handlers.targethandlers;

import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.handler.ITargetTypeHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.targets.TargetType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.MathUtil;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isDoor;

/**
 * Target enemy or ally if force attacking.
 * @author Nik
 */
public class Enemy implements ITargetTypeHandler {
	
	@Override
	public WorldObject getTarget(Creature creature, WorldObject currentTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage) {

		if(creature == currentTarget || !(currentTarget instanceof Creature target) || target.isDead()) {
			if(sendMessage) {
				creature.sendPacket(SystemMessageId.INVALID_TARGET);
			}
			return null;
		}

		if (isDoor(target) && !target.isAutoAttackable(creature)) {
			if (sendMessage) {
				creature.sendPacket(SystemMessageId.INVALID_TARGET);
			}
			return null;
		}

		if (target.isAutoAttackable(creature) || forceUse) {
			// Check for cast range if character cannot move. TODO: char will start follow until within castrange, but if his moving is blocked by geodata, this msg will be sent.
			if (dontMove) {
				if (!MathUtil.isInsideRadius3D(creature, target, skill.getCastRange())) {
					if (sendMessage) {
						creature.sendPacket(SystemMessageId.THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_CANCELLED);
					}
					return null;
				}
			}

			if (!GeoEngine.getInstance().canSeeTarget(creature, target)) {
				if (sendMessage) {
					creature.sendPacket(SystemMessageId.CANNOT_SEE_TARGET);
				}
				return null;
			}
			
			// Skills with this target type cannot be used by playables on playables in peace zone, but can be used by and on NPCs.
			if (target.isInsidePeaceZone(creature)) {
				if (sendMessage) {
					creature.sendPacket(SystemMessageId.YOU_CANNOT_USE_SKILLS_THAT_MAY_HARM_OTHER_PLAYERS_IN_HERE);
				}
				return null;
			}

			// Is this check still actual?
			if (forceUse && nonNull(creature.getActingPlayer()) && creature.getActingPlayer().isSiegeFriend(target)) {
				if (sendMessage) {
					creature.sendPacket(SystemMessageId.FORCE_ATTACK_IS_IMPOSSIBLE_AGAINST_A_TEMPORARY_ALLIED_MEMBER_DURING_A_SIEGE);
				}
				return null;
			}
			
			return target;
		}
		
		if (sendMessage) {
			creature.sendPacket(SystemMessageId.INVALID_TARGET);
		}
		return null;
	}

	@Override
	public Enum<TargetType> getTargetType()
	{
		return TargetType.ENEMY;
	}
}
