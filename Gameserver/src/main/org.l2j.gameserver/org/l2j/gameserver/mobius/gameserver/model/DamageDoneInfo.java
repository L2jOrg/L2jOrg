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

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author xban1x
 */
public final class DamageDoneInfo
{
	private final L2PcInstance _attacker;
	private long _damage = 0;
	
	public DamageDoneInfo(L2PcInstance attacker)
	{
		_attacker = attacker;
	}
	
	public L2PcInstance getAttacker()
	{
		return _attacker;
	}
	
	public void addDamage(long damage)
	{
		_damage += damage;
	}
	
	public long getDamage()
	{
		return _damage;
	}
	
	@Override
	public final boolean equals(Object obj)
	{
		return (this == obj) || ((obj instanceof DamageDoneInfo) && (((DamageDoneInfo) obj).getAttacker() == _attacker));
	}
	
	@Override
	public final int hashCode()
	{
		return _attacker.getObjectId();
	}
}
