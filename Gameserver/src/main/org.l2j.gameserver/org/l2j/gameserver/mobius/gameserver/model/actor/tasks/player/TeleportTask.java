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

import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author UnAfraid
 */
public class TeleportTask implements Runnable
{
	private final L2PcInstance _activeChar;
	private final Location _loc;
	
	public TeleportTask(L2PcInstance player, Location loc)
	{
		_activeChar = player;
		_loc = loc;
	}
	
	@Override
	public void run()
	{
		if ((_activeChar != null) && _activeChar.isOnline())
		{
			_activeChar.teleToLocation(_loc, true);
		}
	}
}
