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

import java.util.Arrays;
import java.util.List;

import com.l2jmobius.gameserver.enums.SpeedType;
import com.l2jmobius.gameserver.enums.StatModifierType;
import com.l2jmobius.gameserver.model.StatsSet;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.effects.AbstractEffect;
import com.l2jmobius.gameserver.model.skills.Skill;
import com.l2jmobius.gameserver.model.stats.Stats;

/**
 * @author Sdw
 */
public final class Speed extends AbstractEffect
{
	private final double _amount;
	private final StatModifierType _mode;
	private List<SpeedType> _speedType;
	
	public Speed(StatsSet params)
	{
		_amount = params.getDouble("amount", 0);
		_mode = params.getEnum("mode", StatModifierType.class, StatModifierType.DIFF);
		_speedType = params.getEnumList("weaponType", SpeedType.class);
		if (_speedType == null)
		{
			_speedType = Arrays.asList(SpeedType.ALL);
		}
	}
	
	@Override
	public void pump(L2Character effected, Skill skill)
	{
		switch (_mode)
		{
			case DIFF:
			{
				for (SpeedType type : _speedType)
				{
					switch (type)
					{
						case RUN:
						{
							effected.getStat().mergeAdd(Stats.RUN_SPEED, _amount);
							break;
						}
						case WALK:
						{
							effected.getStat().mergeAdd(Stats.WALK_SPEED, _amount);
							break;
						}
						case SWIM_RUN:
						{
							effected.getStat().mergeAdd(Stats.SWIM_RUN_SPEED, _amount);
							break;
						}
						case SWIM_WALK:
						{
							effected.getStat().mergeAdd(Stats.SWIM_WALK_SPEED, _amount);
							break;
						}
						case FLY_RUN:
						{
							effected.getStat().mergeAdd(Stats.FLY_RUN_SPEED, _amount);
							break;
						}
						case FLY_WALK:
						{
							effected.getStat().mergeAdd(Stats.FLY_WALK_SPEED, _amount);
							break;
						}
						default:
						{
							effected.getStat().mergeAdd(Stats.RUN_SPEED, _amount);
							effected.getStat().mergeAdd(Stats.WALK_SPEED, _amount);
							effected.getStat().mergeAdd(Stats.SWIM_RUN_SPEED, _amount);
							effected.getStat().mergeAdd(Stats.SWIM_WALK_SPEED, _amount);
							effected.getStat().mergeAdd(Stats.FLY_RUN_SPEED, _amount);
							effected.getStat().mergeAdd(Stats.FLY_WALK_SPEED, _amount);
							break;
						}
					}
				}
				break;
			}
			case PER:
			{
				for (SpeedType type : _speedType)
				{
					switch (type)
					{
						case RUN:
						{
							effected.getStat().mergeMul(Stats.RUN_SPEED, (_amount / 100) + 1);
							break;
						}
						case WALK:
						{
							effected.getStat().mergeMul(Stats.WALK_SPEED, (_amount / 100) + 1);
							break;
						}
						case SWIM_RUN:
						{
							effected.getStat().mergeMul(Stats.SWIM_RUN_SPEED, (_amount / 100) + 1);
							break;
						}
						case SWIM_WALK:
						{
							effected.getStat().mergeMul(Stats.SWIM_WALK_SPEED, (_amount / 100) + 1);
							break;
						}
						case FLY_RUN:
						{
							effected.getStat().mergeMul(Stats.FLY_RUN_SPEED, (_amount / 100) + 1);
							break;
						}
						case FLY_WALK:
						{
							effected.getStat().mergeMul(Stats.FLY_WALK_SPEED, (_amount / 100) + 1);
							break;
						}
						default:
						{
							effected.getStat().mergeMul(Stats.RUN_SPEED, (_amount / 100) + 1);
							effected.getStat().mergeMul(Stats.WALK_SPEED, (_amount / 100) + 1);
							effected.getStat().mergeMul(Stats.SWIM_RUN_SPEED, (_amount / 100) + 1);
							effected.getStat().mergeMul(Stats.SWIM_WALK_SPEED, (_amount / 100) + 1);
							effected.getStat().mergeMul(Stats.FLY_RUN_SPEED, (_amount / 100) + 1);
							effected.getStat().mergeMul(Stats.FLY_WALK_SPEED, (_amount / 100) + 1);
							break;
						}
					}
				}
				break;
			}
		}
	}
}
