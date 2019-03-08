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
package org.l2j.gameserver.mobius.gameserver.model;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2ControllableMobInstance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author littlecrow
 */
public class MobGroupTable
{
	private final Map<Integer, MobGroup> _groupMap = new ConcurrentHashMap<>();
	
	public static final int FOLLOW_RANGE = 300;
	public static final int RANDOM_RANGE = 300;
	
	protected MobGroupTable()
	{
	}
	
	public static MobGroupTable getInstance()
	{
		return SingletonHolder._instance;
	}
	
	public void addGroup(int groupKey, MobGroup group)
	{
		_groupMap.put(groupKey, group);
	}
	
	public MobGroup getGroup(int groupKey)
	{
		return _groupMap.get(groupKey);
	}
	
	public int getGroupCount()
	{
		return _groupMap.size();
	}
	
	public MobGroup getGroupForMob(L2ControllableMobInstance mobInst)
	{
		for (MobGroup mobGroup : _groupMap.values())
		{
			if (mobGroup.isGroupMember(mobInst))
			{
				return mobGroup;
			}
		}
		
		return null;
	}
	
	public MobGroup[] getGroups()
	{
		return _groupMap.values().toArray(new MobGroup[_groupMap.size()]);
	}
	
	public boolean removeGroup(int groupKey)
	{
		return _groupMap.remove(groupKey) != null;
	}
	
	private static class SingletonHolder
	{
		protected static final MobGroupTable _instance = new MobGroupTable();
	}
}