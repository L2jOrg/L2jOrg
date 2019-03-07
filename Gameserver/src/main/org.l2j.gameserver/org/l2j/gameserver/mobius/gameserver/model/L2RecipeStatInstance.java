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

import com.l2jmobius.gameserver.enums.StatType;

/**
 * This class describes a RecipeList statUse and altStatChange component.
 */
public class L2RecipeStatInstance
{
	/** The Identifier of the statType */
	private final StatType _type;
	
	/** The value of the statType */
	private final int _value;
	
	/**
	 * Constructor of L2RecipeStatInstance.
	 * @param type
	 * @param value
	 */
	public L2RecipeStatInstance(String type, int value)
	{
		_type = Enum.valueOf(StatType.class, type);
		_value = value;
	}
	
	/**
	 * @return the the type of the L2RecipeStatInstance.
	 */
	public StatType getType()
	{
		return _type;
	}
	
	/**
	 * @return the value of the L2RecipeStatInstance.
	 */
	public int getValue()
	{
		return _value;
	}
}
