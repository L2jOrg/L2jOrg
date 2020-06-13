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
package handlers.voicedcommandhandlers;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.handler.IVoicedCommandHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author Zoey76
 */
public class SetVCmd implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"set name",
		"set home",
		"set group"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String params)
	{
		if (command.equals("set"))
		{
			final WorldObject target = activeChar.getTarget();
			if (!isPlayer(target))
			{
				return false;
			}
			
			final Player player = activeChar.getTarget().getActingPlayer();
			if ((activeChar.getClan() == null) || (player.getClan() == null) || (activeChar.getClan().getId() != player.getClan().getId()))
			{
				return false;
			}
			
			if (params.startsWith("privileges"))
			{
				final String val = params.substring(11);
				if (!Util.isInteger(val))
				{
					return false;
				}
				
				final int n = Integer.parseInt(val);
				if ((activeChar.getClanPrivileges().getBitmask() <= n) || !activeChar.isClanLeader())
				{
					return false;
				}
				
				player.getClanPrivileges().setBitmask(n);
				activeChar.sendMessage("Your clan privileges have been set to " + n + " by " + activeChar.getName() + ".");
			}
			else if (params.startsWith("title"))
			{
				// TODO why is this empty?
			}
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
