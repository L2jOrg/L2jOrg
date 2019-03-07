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
package org.l2j.gameserver.mobius.gameserver.model.actor.tasks.player;

import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.network.SystemMessageId;
import com.l2jmobius.gameserver.network.serverpackets.SystemMessage;

import java.util.concurrent.TimeUnit;

/**
 * Task dedicated to warn user to take a break.
 * @author UnAfraid
 */
public final class WarnUserTakeBreakTask implements Runnable
{
	private final L2PcInstance _player;
	
	public WarnUserTakeBreakTask(L2PcInstance player)
	{
		_player = player;
	}
	
	@Override
	public void run()
	{
		if (_player != null)
		{
			if (_player.isOnline())
			{
				final long hours = TimeUnit.MILLISECONDS.toHours(_player.getUptime());
				_player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_BEEN_PLAYING_FOR_S1_HOUR_S_HOW_ABOUT_MAKING_A_BREAK).addLong(hours));
			}
			else
			{
				_player.stopWarnUserTakeBreak();
			}
		}
	}
}
