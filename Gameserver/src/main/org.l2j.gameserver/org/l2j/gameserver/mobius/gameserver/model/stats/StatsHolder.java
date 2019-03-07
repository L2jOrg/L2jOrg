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
package org.l2j.gameserver.mobius.gameserver.model.stats;

import com.l2jmobius.gameserver.model.actor.L2Character;

import java.util.function.BiPredicate;

/**
 * @author UnAfraid
 */
public class StatsHolder
{
	private final Stats _stat;
	private final double _value;
	private final BiPredicate<L2Character, StatsHolder> _condition;
	
	public StatsHolder(Stats stat, double value, BiPredicate<L2Character, StatsHolder> condition)
	{
		_stat = stat;
		_value = value;
		_condition = condition;
	}
	
	public StatsHolder(Stats stat, double value)
	{
		this(stat, value, null);
	}
	
	public Stats getStat()
	{
		return _stat;
	}
	
	public double getValue()
	{
		return _value;
	}
	
	public boolean verifyCondition(L2Character creature)
	{
		return (_condition == null) || _condition.test(creature, this);
	}
}
