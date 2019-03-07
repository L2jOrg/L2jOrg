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
package org.l2j.gameserver.mobius.gameserver.script;

/**
 * @author Zoey76
 */
public class EventDrop
{
	private final int[] _itemIdList;
	private final long _minCount;
	private final long _maxCount;
	private final int _dropChance;
	
	public EventDrop(int[] itemIdList, long min, long max, int dropChance)
	{
		_itemIdList = itemIdList;
		_minCount = min;
		_maxCount = max;
		_dropChance = dropChance;
	}
	
	public EventDrop(int itemId, long minCount, long maxCount, int dropChance)
	{
		_itemIdList = new int[]
		{
			itemId
		};
		_minCount = minCount;
		_maxCount = maxCount;
		_dropChance = dropChance;
	}
	
	/**
	 * @return the _itemId
	 */
	public int[] getItemIdList()
	{
		return _itemIdList;
	}
	
	/**
	 * @return the _minCount
	 */
	public long getMinCount()
	{
		return _minCount;
	}
	
	/**
	 * @return the _maxCount
	 */
	public long getMaxCount()
	{
		return _maxCount;
	}
	
	/**
	 * @return the _dropChance
	 */
	public int getDropChance()
	{
		return _dropChance;
	}
}
