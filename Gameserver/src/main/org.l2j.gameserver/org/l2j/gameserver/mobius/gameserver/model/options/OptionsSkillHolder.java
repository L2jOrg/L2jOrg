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
package org.l2j.gameserver.mobius.gameserver.model.options;

import com.l2jmobius.gameserver.model.holders.SkillHolder;

/**
 * @author UnAfraid
 */
public class OptionsSkillHolder extends SkillHolder
{
	private final OptionsSkillType _type;
	private final double _chance;
	
	/**
	 * @param skillId
	 * @param skillLvl
	 * @param type
	 * @param chance
	 */
	public OptionsSkillHolder(int skillId, int skillLvl, double chance, OptionsSkillType type)
	{
		super(skillId, skillLvl);
		_chance = chance;
		_type = type;
	}
	
	public OptionsSkillType getSkillType()
	{
		return _type;
	}
	
	public double getChance()
	{
		return _chance;
	}
}
