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
package handlers.admincommandhandlers;

import org.l2j.gameserver.engine.olympiad.OlympiadEngine;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.olympiad.*;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.world.World;

import java.util.StringTokenizer;

import static org.l2j.commons.util.Util.isDigit;

/**
 * @author UnAfraid
 */
public class AdminOlympiad implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_olympiad_game",
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command);
		final String cmd = st.nextToken();
		switch (cmd)
		{
			case "admin_olympiad_game":
			{
				if (!st.hasMoreTokens())
				{
					BuilderUtil.sendSysMessage(activeChar, "Syntax: //olympiad_game <player name>");
					return false;
				}
				
				final Player player = World.getInstance().findPlayer(st.nextToken());
				if (player == null)
				{
					activeChar.sendPacket(SystemMessageId.YOUR_TARGET_CANNOT_BE_FOUND);
					return false;
				}
				
				if (player == activeChar)
				{
					activeChar.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_ON_YOURSELF);
					return false;
				}
				
				if (!checkplayer(player, activeChar) || !checkplayer(activeChar, activeChar))
				{
					return false;
				}
				
				for (int i = 0; i < OlympiadGameManager.getInstance().getNumberOfStadiums(); i++)
				{
					final OlympiadGameTask task = OlympiadGameManager.getInstance().getOlympiadTask(i);
					if (task != null)
					{
						synchronized (task)
						{
							if (!task.isRunning())
							{
								final Participant[] players = new Participant[2];
								players[0] = new Participant(activeChar, 1);
								players[1] = new Participant(player, 2);
								task.attachGame(new OlympiadGameNonClassed(i, players));
								return true;
							}
						}
					}
				}
				break;
			}
		}
		return false;
	}
	
	private int parseInt(StringTokenizer st, int defaultVal)
	{
		final String token = st.nextToken();
		if (!isDigit(token))
		{
			return -1;
		}
		return Integer.decode(token);
	}
	
	private boolean checkplayer(Player player, Player activeChar)
	{
		if (player.isSubClassActive())
		{
			BuilderUtil.sendSysMessage(activeChar, "Player " + player + " subclass active.");
			return false;
		}
		else if (player.getClassId().level() < 3)
		{
			BuilderUtil.sendSysMessage(activeChar, "Player " + player + " has not 3rd class.");
			return false;
		}
		else if (OlympiadEngine.getInstance().getOlympiadPoints(player) <= 0)
		{
			BuilderUtil.sendSysMessage(activeChar, "Player " + player + " has 0 oly points (add them with (//addolypoints).");
			return false;
		}
		else if (OlympiadManager.getInstance().isRegistered(player))
		{
			BuilderUtil.sendSysMessage(activeChar, "Player " + player + " registered to oly.");
			return false;
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
