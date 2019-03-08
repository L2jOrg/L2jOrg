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

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;

/**
 * Task dedicated to update player's current pvp status.
 * @author UnAfraid
 */
public class PvPFlagTask implements Runnable
{
	private final L2PcInstance _player;
	
	public PvPFlagTask(L2PcInstance player)
	{
		_player = player;
	}
	
	@Override
	public void run()
	{
		if (_player == null)
		{
			return;
		}
		
		if (System.currentTimeMillis() > _player.getPvpFlagLasts())
		{
			_player.stopPvPFlag();
		}
		else if (System.currentTimeMillis() > (_player.getPvpFlagLasts() - 20000))
		{
			_player.updatePvPFlag(2);
		}
		else
		{
			_player.updatePvPFlag(1);
		}
	}
}