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
package handlers.voicedcommandhandlers;

import org.l2j.gameserver.Config;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.handler.IVoicedCommandHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.tasks.player.AutoPotionTask;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogout;

import java.util.HashMap;
import java.util.concurrent.Future;

/**
 * @author Gigi, Mobius
 */
public class AutoPotion implements IVoicedCommandHandler
{
	private static final HashMap<Integer, Future<?>> AUTO_POTION_TASKS = new HashMap<>();
	private static final int POTION_TASK_DELAY = 1000; // 1 second
	
	private static final String[] VOICED_COMMANDS =
	{
		"apon",
		"apoff"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if (!Config.AUTO_POTIONS_ENABLED || (activeChar == null))
		{
			return false;
		}
		if (activeChar.getLevel() < Config.AUTO_POTION_MIN_LVL)
		{
			activeChar.sendMessage("You need to be at least " + Config.AUTO_POTION_MIN_LVL + " to use auto potions.");
			return false;
		}
		
		final int playerOID = activeChar.getObjectId();
		if (command.equals("apon"))
		{
			if (AUTO_POTION_TASKS.containsKey(playerOID))
			{
				AUTO_POTION_TASKS.get(playerOID).cancel(true);
				AUTO_POTION_TASKS.remove(playerOID);
			}
			AUTO_POTION_TASKS.put(activeChar.getObjectId(), ThreadPool.scheduleAtFixedRate(new AutoPotionTask(activeChar), POTION_TASK_DELAY, POTION_TASK_DELAY));
			activeChar.sendMessage("Auto potions is enabled.");
			return true;
		}
		else if (command.equals("apoff"))
		{
			if (AUTO_POTION_TASKS.containsKey(playerOID))
			{
				AUTO_POTION_TASKS.get(playerOID).cancel(true);
				AUTO_POTION_TASKS.remove(playerOID);
			}
			activeChar.sendMessage("Auto potions is disabled.");
		}
		return false;
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGOUT)
	@RegisterType(ListenerRegisterType.GLOBAL)
	public void OnPlayerLogout(OnPlayerLogout event)
	{
		final int playerOID = event.getActiveChar().getObjectId();
		if (AUTO_POTION_TASKS.containsKey(playerOID))
		{
			AUTO_POTION_TASKS.get(playerOID).cancel(true);
			AUTO_POTION_TASKS.remove(playerOID);
		}
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}