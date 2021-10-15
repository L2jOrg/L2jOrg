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

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.handler.ITargetTypeHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Chest;
import org.l2j.gameserver.model.skills.targets.TargetType;
import org.l2j.gameserver.network.SystemMessageId;

import static org.l2j.gameserver.util.GameUtils.isDoor;

/**
 * Target door or treasure chest.
 * @author UnAfraid
 */
public class DoorTreasure implements ITargetTypeHandler
{
	@Override
	public Enum<TargetType> getTargetType()
	{
		return TargetType.DOOR_TREASURE;
	}
	
	@Override
	public WorldObject getTarget(Creature creature, WorldObject currentTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage)
	{
		final WorldObject target = creature.getTarget();
		if ((target != null) && (isDoor(target) || (target instanceof Chest)))
		{
			return target;
		}
		
		if (sendMessage)
		{
			creature.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
		}
		
		return null;
	}
}
