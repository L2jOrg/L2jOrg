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
package handlers.effecthandlers;

import com.l2jmobius.gameserver.model.StatsSet;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.effects.AbstractEffect;
import com.l2jmobius.gameserver.model.skills.Skill;
import com.l2jmobius.gameserver.model.stats.BaseStats;
import com.l2jmobius.gameserver.model.stats.Stats;

/**
 * @author Sdw
 */
public class StatUp extends AbstractEffect
{
	private final BaseStats _stat;
	private final double _amount;
	
	public StatUp(StatsSet params)
	{
		_amount = params.getDouble("amount", 0);
		_stat = params.getEnum("stat", BaseStats.class, BaseStats.STR);
	}
	
	@Override
	public void pump(L2Character effected, Skill skill)
	{
		Stats stat = Stats.STAT_STR;
		
		switch (_stat)
		{
			case INT:
			{
				stat = Stats.STAT_INT;
				break;
			}
			case DEX:
			{
				stat = Stats.STAT_DEX;
				break;
			}
			case WIT:
			{
				stat = Stats.STAT_WIT;
				break;
			}
			case CON:
			{
				stat = Stats.STAT_CON;
				break;
			}
			case MEN:
			{
				stat = Stats.STAT_MEN;
				break;
			}
		}
		effected.getStat().mergeAdd(stat, _amount);
	}
}
