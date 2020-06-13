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
package handlers.bypasshandlers;

import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExShowVariationCancelWindow;
import org.l2j.gameserver.network.serverpackets.ExShowVariationMakeWindow;

import static org.l2j.gameserver.util.GameUtils.isNpc;

public class Augment implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"Augment"
	};
	
	@Override
	public boolean useBypass(String command, Player player, Creature target)
	{
		if (!isNpc(target))
		{
			return false;
		}
		
		try
		{
			switch (Integer.parseInt(command.substring(8, 9).trim()))
			{
				case 1:
				{
					player.sendPacket(ExShowVariationMakeWindow.STATIC_PACKET);
					return true;
				}
				case 2:
				{
					player.sendPacket(ExShowVariationCancelWindow.STATIC_PACKET);
					return true;
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.warn("Exception in " + getClass().getSimpleName(), e);
		}
		return false;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}
