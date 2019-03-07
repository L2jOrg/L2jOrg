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

import com.l2jmobius.gameserver.data.xml.impl.OptionData;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.options.Options;

import java.util.Objects;

/**
 * Used to store an augmentation and its bonuses.
 * @author durgus, UnAfraid, Pere
 */
public final class VariationInstance
{
	private final int _mineralId;
	private final Options _option1;
	private final Options _option2;
	
	public VariationInstance(int mineralId, int option1Id, int option2Id)
	{
		_mineralId = mineralId;
		_option1 = OptionData.getInstance().getOptions(option1Id);
		_option2 = OptionData.getInstance().getOptions(option2Id);
		if ((_option1 == null) || (_option2 == null))
		{
			throw new IllegalArgumentException("Couldn't find option for id: " + option1Id + " or id: " + option1Id);
		}
	}
	
	public VariationInstance(int mineralId, Options op1, Options op2)
	{
		Objects.requireNonNull(op1);
		Objects.requireNonNull(op2);
		
		_mineralId = mineralId;
		_option1 = op1;
		_option2 = op2;
	}
	
	public int getMineralId()
	{
		return _mineralId;
	}
	
	public int getOption1Id()
	{
		return _option1.getId();
	}
	
	public int getOption2Id()
	{
		return _option2.getId();
	}
	
	public void applyBonus(L2PcInstance player)
	{
		_option1.apply(player);
		_option2.apply(player);
	}
	
	public void removeBonus(L2PcInstance player)
	{
		_option1.remove(player);
		_option2.remove(player);
	}
}
