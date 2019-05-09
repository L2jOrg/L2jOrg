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

import org.l2j.gameserver.enums.ReduceDropType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Stats;

/**
 * @author Sdw
 */
public class ReduceDropPenalty extends AbstractEffect
{
	private final double _exp;
	private final double _deathPenalty;
	private final ReduceDropType _type;
	
	public ReduceDropPenalty(StatsSet params)
	{
		_exp = params.getDouble("exp", 0);
		_deathPenalty = params.getDouble("deathPenalty", 0);
		_type = params.getEnum("type", ReduceDropType.class, ReduceDropType.MOB);
	}
	
	@Override
	public void pump(L2Character effected, Skill skill) {
		switch (_type) {
			case MOB -> reduce(effected, Stats.REDUCE_EXP_LOST_BY_MOB, Stats.REDUCE_DEATH_PENALTY_BY_MOB);
			case PK -> reduce(effected, Stats.REDUCE_EXP_LOST_BY_PVP, Stats.REDUCE_DEATH_PENALTY_BY_PVP);
			case RAID -> reduce(effected, Stats.REDUCE_EXP_LOST_BY_RAID, Stats.REDUCE_DEATH_PENALTY_BY_RAID);
			case ANY ->  {
				reduce(effected, Stats.REDUCE_EXP_LOST_BY_MOB, Stats.REDUCE_DEATH_PENALTY_BY_MOB);
				reduce(effected, Stats.REDUCE_EXP_LOST_BY_PVP, Stats.REDUCE_DEATH_PENALTY_BY_PVP);
				reduce(effected, Stats.REDUCE_EXP_LOST_BY_RAID, Stats.REDUCE_DEATH_PENALTY_BY_RAID);
			}
		}
	}

	private void reduce(L2Character effected, Stats statExp, Stats statPenalty) {
		effected.getStat().mergeMul(statExp, (_exp / 100) + 1);
		effected.getStat().mergeMul(statPenalty, (_deathPenalty / 100) + 1);
	}
}
