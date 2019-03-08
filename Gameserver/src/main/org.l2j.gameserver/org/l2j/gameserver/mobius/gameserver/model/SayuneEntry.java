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

import org.l2j.gameserver.mobius.gameserver.model.interfaces.ILocational;

import java.util.LinkedList;
import java.util.List;

/**
 * @author UnAfraid
 */
public class SayuneEntry implements ILocational
{
	private boolean _isSelector = false;
	private final int _id;
	private int _x;
	private int _y;
	private int _z;
	private final List<SayuneEntry> _innerEntries = new LinkedList<>();
	
	public SayuneEntry(int id)
	{
		_id = id;
	}
	
	public SayuneEntry(boolean isSelector, int id, int x, int y, int z)
	{
		_isSelector = isSelector;
		_id = id;
		_x = x;
		_y = y;
		_z = z;
	}
	
	public int getId()
	{
		return _id;
	}
	
	@Override
	public int getX()
	{
		return _x;
	}
	
	@Override
	public int getY()
	{
		return _y;
	}
	
	@Override
	public int getZ()
	{
		return _z;
	}
	
	@Override
	public int getHeading()
	{
		return 0;
	}
	
	@Override
	public ILocational getLocation()
	{
		return new Location(_x, _y, _z);
	}
	
	public boolean isSelector()
	{
		return _isSelector;
	}
	
	public List<SayuneEntry> getInnerEntries()
	{
		return _innerEntries;
	}
	
	public SayuneEntry addInnerEntry(SayuneEntry innerEntry)
	{
		_innerEntries.add(innerEntry);
		return innerEntry;
	}
}
