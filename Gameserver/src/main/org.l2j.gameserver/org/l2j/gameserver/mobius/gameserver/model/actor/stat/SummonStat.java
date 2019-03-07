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
package org.l2j.gameserver.mobius.gameserver.model.actor.stat;

import com.l2jmobius.Config;
import com.l2jmobius.gameserver.model.actor.L2Summon;

public class SummonStat extends PlayableStat
{
	public SummonStat(L2Summon activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public L2Summon getActiveChar()
	{
		return (L2Summon) super.getActiveChar();
	}
	
	@Override
	public double getRunSpeed()
	{
		final double val = super.getRunSpeed() + Config.RUN_SPD_BOOST;
		
		// Apply max run speed cap.
		if (val > (Config.MAX_RUN_SPEED + 50)) // In retail maximum run speed is 350 for summons and 300 for players
		{
			return Config.MAX_RUN_SPEED + 50;
		}
		
		return val;
	}
	
	@Override
	public double getWalkSpeed()
	{
		final double val = super.getWalkSpeed() + Config.RUN_SPD_BOOST;
		
		// Apply max run speed cap.
		if (val > (Config.MAX_RUN_SPEED + 50)) // In retail maximum run speed is 350 for summons and 300 for players
		{
			return Config.MAX_RUN_SPEED + 50;
		}
		
		return val;
	}
}
