/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
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
import org.l2j.gameserver.model.entity.Event;

import static org.l2j.gameserver.util.GameUtils.isNpc;

public class EventEngine implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"event_participate",
		"event_unregister"
	};
	
	@Override
	public boolean useBypass(String command, Player activeChar, Creature target)
	{
		if (!isNpc(target))
		{
			return false;
		}
		
		try
		{
			if (command.equalsIgnoreCase("event_participate"))
			{
				Event.registerPlayer(activeChar);
				return true;
			}
			else if (command.equalsIgnoreCase("event_unregister"))
			{
				Event.removeAndResetPlayer(activeChar);
				return true;
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
