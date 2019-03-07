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

import java.util.Calendar;

/**
 * @author UnAfraid
 */
public class SiegeScheduleDate
{
	private final int _day;
	private final int _hour;
	private final int _maxConcurrent;
	
	public SiegeScheduleDate(StatsSet set)
	{
		_day = set.getInt("day", Calendar.SUNDAY);
		_hour = set.getInt("hour", 16);
		_maxConcurrent = set.getInt("maxConcurrent", 5);
	}
	
	public int getDay()
	{
		return _day;
	}
	
	public int getHour()
	{
		return _hour;
	}
	
	public int getMaxConcurrent()
	{
		return _maxConcurrent;
	}
}
