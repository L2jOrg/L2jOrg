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
package handlers.admincommandhandlers;

import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.ControllableMob;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.taskmanager.DecayTaskManager;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.world.World;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * This class handles following admin commands: - res = resurrects target Creature
 * @version $Revision: 1.2.4.5 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminRes implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_res",
		"admin_res_monster"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (command.startsWith("admin_res "))
		{
			handleRes(activeChar, command.split(" ")[1]);
		}
		else if (command.equals("admin_res"))
		{
			handleRes(activeChar);
		}
		else if (command.startsWith("admin_res_monster "))
		{
			handleNonPlayerRes(activeChar, command.split(" ")[1]);
		}
		else if (command.equals("admin_res_monster"))
		{
			handleNonPlayerRes(activeChar);
		}
		
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private void handleRes(Player activeChar)
	{
		handleRes(activeChar, null);
	}
	
	private void handleRes(Player activeChar, String resParam)
	{
		WorldObject obj = activeChar.getTarget();
		
		if (resParam != null)
		{
			// Check if a player name was specified as a param.
			final Player plyr = World.getInstance().findPlayer(resParam);
			
			if (plyr != null)
			{
				obj = plyr;
			}
			else
			{
				// Otherwise, check if the param was a radius.
				try
				{
					final int radius = Integer.parseInt(resParam);
					
					World.getInstance().forEachVisibleObjectInRange(activeChar, Player.class, radius, this::doResurrect);
					
					BuilderUtil.sendSysMessage(activeChar, "Resurrected all players within a " + radius + " unit radius.");
					return;
				}
				catch (NumberFormatException e)
				{
					BuilderUtil.sendSysMessage(activeChar, "Enter a valid player name or radius.");
					return;
				}
			}
		}
		
		if (obj == null)
		{
			obj = activeChar;
		}
		
		if (obj instanceof ControllableMob)
		{
			activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		doResurrect((Creature) obj);
	}
	
	private void handleNonPlayerRes(Player activeChar)
	{
		handleNonPlayerRes(activeChar, "");
	}
	
	private void handleNonPlayerRes(Player activeChar, String radiusStr)
	{
		final WorldObject obj = activeChar.getTarget();
		
		try
		{
			int radius = 0;
			
			if (!radiusStr.isEmpty())
			{
				radius = Integer.parseInt(radiusStr);
				
				World.getInstance().forEachVisibleObjectInRange(activeChar, Creature.class, radius, knownChar ->
				{
					if (!isPlayer(knownChar) && !(knownChar instanceof ControllableMob))
					{
						doResurrect(knownChar);
					}
				});
				
				BuilderUtil.sendSysMessage(activeChar, "Resurrected all non-players within a " + radius + " unit radius.");
			}
		}
		catch (NumberFormatException e)
		{
			BuilderUtil.sendSysMessage(activeChar, "Enter a valid radius.");
			return;
		}
		
		if ((obj == null) || (isPlayer(obj)) || (obj instanceof ControllableMob))
		{
			activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		doResurrect((Creature) obj);
	}
	
	private void doResurrect(Creature targetChar)
	{
		if (!targetChar.isDead())
		{
			return;
		}
		
		// If the target is a player, then restore the XP lost on death.
		if (isPlayer(targetChar))
		{
			((Player) targetChar).restoreExp(100.0);
		}
		else
		{
			DecayTaskManager.getInstance().cancel(targetChar);
		}
		
		targetChar.doRevive();
	}
}
