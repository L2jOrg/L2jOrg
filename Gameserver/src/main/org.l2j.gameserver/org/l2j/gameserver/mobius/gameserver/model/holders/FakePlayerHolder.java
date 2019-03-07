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

import com.l2jmobius.gameserver.model.StatsSet;

/**
 * @author Mobius
 */
public class FakePlayerHolder
{
	private final int _classId;
	private final int _hair;
	private final int _hairColor;
	private final int _face;
	private final int _nameColor;
	private final int _titleColor;
	private final int _equipHead;
	private final int _equipRHand;
	private final int _equipLHand;
	private final int _equipGloves;
	private final int _equipChest;
	private final int _equipLegs;
	private final int _equipFeet;
	private final int _equipCloak;
	private final int _equipHair;
	private final int _equipHair2;
	private final int _agathionId;
	private final int _weaponEnchantLevel;
	private final int _armorEnchantLevel;
	private final boolean _fishing;
	private final int _baitLocationX;
	private final int _baitLocationY;
	private final int _baitLocationZ;
	private final int _recommends;
	private final int _nobleLevel;
	private final boolean _hero;
	private final int _clanId;
	private final int _pledgeStatus;
	
	public FakePlayerHolder(StatsSet set)
	{
		_classId = set.getInt("classId", 182);
		
		_hair = set.getInt("hair", 1);
		_hairColor = set.getInt("hairColor", 1);
		_face = set.getInt("face", 1);
		
		_nameColor = set.getInt("nameColor", 0xFFFFFF);
		_titleColor = set.getInt("titleColor", 0xECF9A2);
		
		_equipHead = set.getInt("equipHead", 0);
		_equipRHand = set.getInt("equipRHand", 0); // or dual hand
		_equipLHand = set.getInt("equipLHand", 0);
		_equipGloves = set.getInt("equipGloves", 0);
		_equipChest = set.getInt("equipChest", 0);
		_equipLegs = set.getInt("equipLegs", 0);
		_equipFeet = set.getInt("equipFeet", 0);
		_equipCloak = set.getInt("equipCloak", 0);
		_equipHair = set.getInt("equipHair", 0);
		_equipHair2 = set.getInt("equipHair2", 0);
		_agathionId = set.getInt("agathionId", 0);
		
		_weaponEnchantLevel = set.getInt("weaponEnchantLevel", 0);
		_armorEnchantLevel = set.getInt("armorEnchantLevel", 0);
		
		_fishing = set.getBoolean("fishing", false);
		_baitLocationX = set.getInt("baitLocationX", 0);
		_baitLocationY = set.getInt("baitLocationY", 0);
		_baitLocationZ = set.getInt("baitLocationZ", 0);
		
		_recommends = set.getInt("recommends", 0);
		_nobleLevel = set.getInt("nobleLevel", 0);
		_hero = set.getBoolean("hero", false);
		_clanId = set.getInt("clanId", 0);
		_pledgeStatus = set.getInt("pledgeStatus", 0);
	}
	
	public int getClassId()
	{
		return _classId;
	}
	
	public int getHair()
	{
		return _hair;
	}
	
	public int getHairColor()
	{
		return _hairColor;
	}
	
	public int getFace()
	{
		return _face;
	}
	
	public int getNameColor()
	{
		return _nameColor;
	}
	
	public int getTitleColor()
	{
		return _titleColor;
	}
	
	public int getEquipHead()
	{
		return _equipHead;
	}
	
	public int getEquipRHand()
	{
		return _equipRHand;
	}
	
	public int getEquipLHand()
	{
		return _equipLHand;
	}
	
	public int getEquipGloves()
	{
		return _equipGloves;
	}
	
	public int getEquipChest()
	{
		return _equipChest;
	}
	
	public int getEquipLegs()
	{
		return _equipLegs;
	}
	
	public int getEquipFeet()
	{
		return _equipFeet;
	}
	
	public int getEquipCloak()
	{
		return _equipCloak;
	}
	
	public int getEquipHair()
	{
		return _equipHair;
	}
	
	public int getEquipHair2()
	{
		return _equipHair2;
	}
	
	public int getAgathionId()
	{
		return _agathionId;
	}
	
	public int getWeaponEnchantLevel()
	{
		return _weaponEnchantLevel;
	}
	
	public int getArmorEnchantLevel()
	{
		return _armorEnchantLevel;
	}
	
	public boolean isFishing()
	{
		return _fishing;
	}
	
	public int getBaitLocationX()
	{
		return _baitLocationX;
	}
	
	public int getBaitLocationY()
	{
		return _baitLocationY;
	}
	
	public int getBaitLocationZ()
	{
		return _baitLocationZ;
	}
	
	public int getRecommends()
	{
		return _recommends;
	}
	
	public int getNobleLevel()
	{
		return _nobleLevel;
	}
	
	public boolean isHero()
	{
		return _hero;
	}
	
	public int getClanId()
	{
		return _clanId;
	}
	
	public int getPledgeStatus()
	{
		return _pledgeStatus;
	}
}
