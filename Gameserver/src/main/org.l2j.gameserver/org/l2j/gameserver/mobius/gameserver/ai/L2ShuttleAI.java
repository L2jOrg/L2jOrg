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
package org.l2j.gameserver.mobius.gameserver.ai;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2ShuttleInstance;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.shuttle.ExShuttleMove;

/**
 * @author UnAfraid
 */
public class L2ShuttleAI extends L2VehicleAI
{
	public L2ShuttleAI(L2ShuttleInstance shuttle)
	{
		super(shuttle);
	}
	
	@Override
	public void moveTo(int x, int y, int z)
	{
		if (!_actor.isMovementDisabled())
		{
			_clientMoving = true;
			_actor.moveToLocation(x, y, z, 0);
			_actor.broadcastPacket(new ExShuttleMove(getActor(), x, y, z));
		}
	}
	
	@Override
	public L2ShuttleInstance getActor()
	{
		return (L2ShuttleInstance) _actor;
	}
}
