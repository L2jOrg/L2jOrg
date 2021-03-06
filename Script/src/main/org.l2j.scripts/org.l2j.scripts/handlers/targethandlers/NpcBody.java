/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.scripts.handlers.targethandlers;

import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.handler.ITargetTypeHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.targets.TargetType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.MathUtil;

import static org.l2j.gameserver.util.GameUtils.*;

/**
 * Target dead monster.
 * @author Nik
 */
public class NpcBody implements ITargetTypeHandler
{
	
	@Override
	public Enum<TargetType> getTargetType()
	{
		return TargetType.NPC_BODY;
	}
	
	@Override
	public WorldObject getTarget(Creature creature, WorldObject currentTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage)
	{
		if (!isCreature(currentTarget))
		{
			return null;
		}
		
		if (!isNpc(currentTarget) && !isSummon(currentTarget))
		{
			if (sendMessage)
			{
				creature.sendPacket(SystemMessageId.INVALID_TARGET);
			}
			return null;
		}
		
		final Creature cha = (Creature) currentTarget;
		if (cha.isDead())
		{
			// Check for cast range if character cannot move. TODO: char will start follow until within castrange, but if his moving is blocked by geodata, this msg will be sent.
			if (dontMove)
			{
				if (!MathUtil.isInsideRadius2D(creature, cha, skill.getCastRange()))
				{
					if (sendMessage)
					{
						creature.sendPacket(SystemMessageId.THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_CANCELLED);
					}
					return null;
				}
			}
			
			// Geodata check when character is within range.
			if (!GeoEngine.getInstance().canSeeTarget(creature, cha))
			{
				if (sendMessage)
				{
					creature.sendPacket(SystemMessageId.CANNOT_SEE_TARGET);
				}
				return null;
			}
			return cha;
		}
		
		// If target is not dead or not player/pet it will not even bother to walk within range, unlike Enemy target type.
		if (sendMessage)
		{
			creature.sendPacket(SystemMessageId.INVALID_TARGET);
		}
		return null;
	}
}
