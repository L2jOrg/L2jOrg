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
package org.l2j.gameserver.mobius.gameserver.model.holders;

import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.network.NpcStringId;

/**
 * @author St3eT
 */
public class ClanHallTeleportHolder extends Location
{
	private final NpcStringId _npcStringId;
	private final int _minFunctionLevel;
	private final int _cost;
	
	public ClanHallTeleportHolder(int npcStringId, int x, int y, int z, int minFunctionLevel, int cost)
	{
		super(x, y, z);
		_npcStringId = NpcStringId.getNpcStringId(npcStringId);
		_minFunctionLevel = minFunctionLevel;
		_cost = cost;
	}
	
	public final NpcStringId getNpcStringId()
	{
		return _npcStringId;
	}
	
	public final int getMinFunctionLevel()
	{
		return _minFunctionLevel;
	}
	
	public final int getCost()
	{
		return _cost;
	}
}
