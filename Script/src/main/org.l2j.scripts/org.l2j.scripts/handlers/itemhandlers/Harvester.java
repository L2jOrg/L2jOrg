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
package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;

import static org.l2j.gameserver.util.GameUtils.isMonster;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author l3x
 */
public final class Harvester implements IItemHandler
{
	@Override
	public boolean useItem(Playable playable, Item item, boolean forceUse)
	{
		if (!Config.ALLOW_MANOR)
		{
			return false;
		}
		else if (!isPlayer(playable))
		{
			playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
			return false;
		}
		
		if (!item.hasSkills(ItemSkillType.NORMAL)) {
			LOGGER.warn("item {} is missing skills!", item);
			return false;
		}
		
		var player = playable.getActingPlayer();
		final WorldObject target = player.getTarget();
		if (!isMonster(target) || !((Creature) target).isDead()) {
			player.sendPacket(SystemMessageId.INVALID_TARGET);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		item.forEachSkill(ItemSkillType.NORMAL, s -> player.useSkill(s.getSkill(), item, false, false));
		return true;
	}
}
