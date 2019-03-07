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
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.interfaces.ILocational;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds depending between NPCs spawn point and route
 * @author GKR
 */
public final class NpcRoutesHolder
{
	private final Map<String, String> _correspondences;
	
	public NpcRoutesHolder()
	{
		_correspondences = new HashMap<>();
	}
	
	/**
	 * Add correspondence between specific route and specific spawn point
	 * @param routeName name of route
	 * @param loc Location of spawn point
	 */
	public void addRoute(String routeName, Location loc)
	{
		_correspondences.put(getUniqueKey(loc), routeName);
	}
	
	/**
	 * @param npc
	 * @return route name for given NPC.
	 */
	public String getRouteName(L2Npc npc)
	{
		if (npc.getSpawn() != null)
		{
			final String key = getUniqueKey(npc.getSpawn().getLocation());
			return _correspondences.containsKey(key) ? _correspondences.get(key) : "";
		}
		return "";
	}
	
	/**
	 * @param loc
	 * @return unique text string for given Location.
	 */
	private String getUniqueKey(ILocational loc)
	{
		return (loc.getX() + "-" + loc.getY() + "-" + loc.getZ());
	}
}
