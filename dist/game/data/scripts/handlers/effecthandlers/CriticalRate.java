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
import com.l2jmobius.gameserver.model.skills.Skill;
import com.l2jmobius.gameserver.model.stats.Stats;

/**
 * @author Sdw
 */
public class CriticalRate extends AbstractConditionalHpEffect
{
	
	public CriticalRate(StatsSet params)
	{
		super(params, Stats.CRITICAL_RATE);
	}
	
	@Override
	public void pump(L2Character effected, Skill skill)
	{
		if (_conditions.isEmpty() || _conditions.stream().allMatch(cond -> cond.test(effected, effected, skill)))
		{
			switch (_mode)
			{
				case DIFF:
				{
					effected.getStat().mergeAdd(_addStat, _amount);
					break;
				}
				case PER:
				{
					effected.getStat().mergeMul(_mulStat, (_amount / 100));
					break;
				}
			}
		}
	}
}
