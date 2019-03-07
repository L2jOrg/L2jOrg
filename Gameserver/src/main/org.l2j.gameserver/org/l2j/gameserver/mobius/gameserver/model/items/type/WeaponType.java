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
package org.l2j.gameserver.mobius.gameserver.model.items.type;

import com.l2jmobius.gameserver.model.stats.TraitType;

/**
 * Weapon Type enumerated.
 * @author mkizub
 */
public enum WeaponType implements ItemType
{
	NONE(TraitType.NONE),
	SWORD(TraitType.SWORD),
	BLUNT(TraitType.BLUNT),
	DAGGER(TraitType.DAGGER),
	POLE(TraitType.POLE),
	DUALFIST(TraitType.DUALFIST),
	BOW(TraitType.BOW),
	ETC(TraitType.ETC),
	DUAL(TraitType.DUAL),
	FIST(TraitType.FIST), // 0 items with that type
	FISHINGROD(TraitType.NONE),
	RAPIER(TraitType.RAPIER),
	CROSSBOW(TraitType.CROSSBOW),
	ANCIENTSWORD(TraitType.ANCIENTSWORD),
	FLAG(TraitType.NONE), // 0 items with that type
	DUALDAGGER(TraitType.DUALDAGGER),
	OWNTHING(TraitType.NONE), // 0 items with that type
	TWOHANDCROSSBOW(TraitType.TWOHANDCROSSBOW),
	DUALBLUNT(TraitType.DUALBLUNT);
	
	private final int _mask;
	private final TraitType _traitType;
	
	/**
	 * Constructor of the L2WeaponType.
	 * @param traitType
	 */
	WeaponType(TraitType traitType)
	{
		_mask = 1 << ordinal();
		_traitType = traitType;
	}
	
	/**
	 * @return the ID of the item after applying the mask.
	 */
	@Override
	public int mask()
	{
		return _mask;
	}
	
	/**
	 * @return L2TraitType the type of the WeaponType
	 */
	public TraitType getTraitType()
	{
		return _traitType;
	}
	
	public boolean isRanged()
	{
		return (this == BOW) || (this == CROSSBOW) || (this == TWOHANDCROSSBOW);
	}
	
	public boolean isCrossbow()
	{
		return (this == CROSSBOW) || (this == TWOHANDCROSSBOW);
	}
	
	public boolean isDual()
	{
		return (this == DUALFIST) || (this == DUAL) || (this == DUALDAGGER) || (this == DUALBLUNT);
	}
}
