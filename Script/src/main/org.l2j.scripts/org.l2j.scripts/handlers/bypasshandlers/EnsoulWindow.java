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
package org.l2j.scripts.handlers.bypasshandlers;

import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ensoul.ExShowEnsoulExtractionWindow;
import org.l2j.gameserver.network.serverpackets.ensoul.ExShowEnsoulWindow;

import static org.l2j.gameserver.util.GameUtils.isNpc;

/**
 * @author St3eT
 */
public class EnsoulWindow implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"show_ensoul_window",
		"show_extract_ensoul_window"
	};
	
	@Override
	public boolean useBypass(String command, Player player, Creature target)
	{
		if (!isNpc(target))
		{
			return false;
		}
		
		if (command.toLowerCase().startsWith(COMMANDS[0])) // show_ensoul_window
		{
			player.sendPacket(ExShowEnsoulWindow.STATIC_PACKET);
			return true;
		}
		else if (command.toLowerCase().startsWith(COMMANDS[1])) // show_extract_ensoul_window
		{
			player.sendPacket(ExShowEnsoulExtractionWindow.STATIC_PACKET);
			return true;
		}
		return false;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}
