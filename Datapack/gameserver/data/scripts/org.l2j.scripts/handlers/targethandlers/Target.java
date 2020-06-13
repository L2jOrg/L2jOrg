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
package handlers.targethandlers;

import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.handler.ITargetTypeHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.targets.TargetType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.MathUtil;

import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * Any friendly selected target or enemy if force use. Works on dead targets or doors as well.
 * @author Nik
 */
public class Target implements ITargetTypeHandler
{
	@Override
	public Enum<TargetType> getTargetType()
	{
		return TargetType.TARGET;
	}
	
	@Override
	public WorldObject getTarget(Creature activeChar, WorldObject selectedTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage)
	{
		if (selectedTarget == null)
		{
			return null;
		}
		
		if (!isCreature(selectedTarget))
		{
			if (sendMessage)
			{
				activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
			}
			return null;
		}
		
		final Creature target = (Creature) selectedTarget;
		
		// You can always target yourself.
		if (activeChar == target)
		{
			return target;
		}
		
		// Check for cast range if character cannot move. TODO: char will start follow until within castrange, but if his moving is blocked by geodata, this msg will be sent.
		if (dontMove)
		{
			if (!MathUtil.isInsideRadius2D(activeChar, target, skill.getCastRange()))
			{
				if (sendMessage)
				{
					activeChar.sendPacket(SystemMessageId.THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_CANCELLED);
				}
				
				return null;
			}
		}
		
		if (skill.isFlyType() && !GeoEngine.getInstance().canMoveToTarget(activeChar.getX(), activeChar.getY(), activeChar.getZ(), target.getX(), target.getY(), target.getZ(), activeChar.getInstanceWorld()))
		{
			if (sendMessage)
			{
				activeChar.sendPacket(SystemMessageId.THE_TARGET_IS_LOCATED_WHERE_YOU_CANNOT_CHARGE);
			}
			return null;
		}
		
		// Geodata check when character is within range.
		if (!GeoEngine.getInstance().canSeeTarget(activeChar, target))
		{
			if (sendMessage)
			{
				activeChar.sendPacket(SystemMessageId.CANNOT_SEE_TARGET);
			}
			
			return null;
		}
		return target;
	}
}
