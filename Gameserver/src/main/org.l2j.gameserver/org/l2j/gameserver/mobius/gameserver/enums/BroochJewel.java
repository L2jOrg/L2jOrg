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
package org.l2j.gameserver.mobius.gameserver.enums;

/**
 * @author Mobius
 */
public enum BroochJewel
{
	RUBY_LV1(38855, 17814, 0.01),
	RUBY_LV2(38856, 17814, 0.035),
	RUBY_LV3(38857, 17815, 0.075),
	RUBY_LV4(38858, 17816, 0.125),
	RUBY_LV5(38859, 17817, 0.2),
	SHAPPHIRE_LV1(38927, 17818, 0.01),
	SHAPPHIRE_LV2(38928, 17818, 0.035),
	SHAPPHIRE_LV3(38929, 17819, 0.075),
	SHAPPHIRE_LV4(38930, 17820, 0.125),
	SHAPPHIRE_LV5(38931, 17821, 0.2);
	
	private int _itemId;
	private int _effectId;
	private double _bonus;
	
	private BroochJewel(int itemId, int effectId, double bonus)
	{
		_itemId = itemId;
		_effectId = effectId;
		_bonus = bonus;
	}
	
	public int getItemId()
	{
		return _itemId;
	}
	
	public int getEffectId()
	{
		return _effectId;
	}
	
	public double getBonus()
	{
		return _bonus;
	}
}
