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

/**
 * @author UnAfraid, NosBit
 */
public enum TraitType
{
	NONE(0),
	SWORD(1),
	BLUNT(1),
	DAGGER(1),
	POLE(1),
	FIST(1),
	BOW(1),
	ETC(1),
	UNK_8(0),
	POISON(3),
	HOLD(3),
	BLEED(3),
	SLEEP(3),
	SHOCK(3),
	DERANGEMENT(3),
	BUG_WEAKNESS(2),
	ANIMAL_WEAKNESS(2),
	PLANT_WEAKNESS(2),
	BEAST_WEAKNESS(2),
	DRAGON_WEAKNESS(2),
	PARALYZE(3),
	DUAL(1),
	DUALFIST(1),
	BOSS(3),
	GIANT_WEAKNESS(2),
	CONSTRUCT_WEAKNESS(2),
	DEATH(3),
	VALAKAS(2),
	ANESTHESIA(2),
	CRITICAL_POISON(3),
	ROOT_PHYSICALLY(3),
	ROOT_MAGICALLY(3),
	RAPIER(1),
	CROSSBOW(1),
	ANCIENTSWORD(1),
	TURN_STONE(3),
	GUST(3),
	PHYSICAL_BLOCKADE(3),
	TARGET(3),
	PHYSICAL_WEAKNESS(3),
	MAGICAL_WEAKNESS(3),
	DUALDAGGER(1),
	DUALBLUNT(1),
	KNOCKBACK(3),
	KNOCKDOWN(3),
	PULL(3),
	HATE(3),
	AGGRESSION(3),
	AIRBIND(3),
	DISARM(3),
	DEPORT(3),
	CHANGEBODY(3),
	TWOHANDCROSSBOW(1),
	ZONE(3),
	PSYCHIC(3);
	
	private final int _type; // 1 = weapon, 2 = weakness, 3 = resistance
	
	TraitType(int type)
	{
		_type = type;
	}
	
	public int getType()
	{
		return _type;
	}
}
