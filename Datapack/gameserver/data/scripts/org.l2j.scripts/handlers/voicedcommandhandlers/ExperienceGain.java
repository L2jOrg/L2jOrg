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

import org.l2j.gameserver.handler.IVoicedCommandHandler;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayableExpChanged;
import org.l2j.gameserver.model.events.listeners.FunctionEventListener;
import org.l2j.gameserver.model.events.returns.TerminateReturn;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author xban1x
 */
public final class ExperienceGain implements IVoicedCommandHandler
{
	private final String[] COMMANDS = new String[]
	{
		"expoff",
		"expon",
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String params)
	{
		if (command.equals("expoff"))
		{
			if (!activeChar.getVariables().getBoolean("EXPOFF", false))
			{
				activeChar.addListener(new FunctionEventListener(activeChar, EventType.ON_PLAYABLE_EXP_CHANGED, (OnPlayableExpChanged event) -> onExperienceReceived(event.getPlayable(), event.getNewExp() - event.getOldExp()), this));
				activeChar.getVariables().set("EXPOFF", true);
				activeChar.sendMessage("Experience gain is disabled.");
			}
		}
		else if (command.equals("expon"))
		{
			if (activeChar.getVariables().getBoolean("EXPOFF", false))
			{
				activeChar.removeListenerIf(EventType.ON_PLAYABLE_EXP_CHANGED, listener -> listener.getOwner() == this);
				activeChar.getVariables().set("EXPOFF", false);
				activeChar.sendMessage("Experience gain is enabled.");
			}
		}
		return true;
	}
	
	private TerminateReturn onExperienceReceived(Playable playable, long exp)
	{
		if (isPlayer(playable) && playable.getActingPlayer().isDead())
		{
			return new TerminateReturn(false, false, false);
		}
		return new TerminateReturn(true, true, true);
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}

	public static IVoicedCommandHandler provider() {
		return new ExperienceGain();
	}
}
