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
import com.l2jmobius.gameserver.model.actor.stat.PcStat;
import com.l2jmobius.gameserver.model.effects.AbstractEffect;
import com.l2jmobius.gameserver.model.skills.BuffInfo;
import com.l2jmobius.gameserver.model.skills.Skill;
import com.l2jmobius.gameserver.model.zone.ZoneId;

/**
 * Recover Vitality in Peace Zone effect implementation.
 * @author Mobius
 */
public final class RecoverVitalityInPeaceZone extends AbstractEffect
{
	private final double _amount;
	private final int _ticks;
	
	public RecoverVitalityInPeaceZone(StatsSet params)
	{
		_amount = params.getDouble("amount", 0);
		_ticks = params.getInt("ticks", 10);
	}
	
	@Override
	public int getTicks()
	{
		return _ticks;
	}
	
	@Override
	public boolean onActionTime(L2Character effector, L2Character effected, Skill skill)
	{
		if ((effected == null) //
			|| effected.isDead() //
			|| !effected.isPlayer() //
			|| !effected.isInsideZone(ZoneId.PEACE))
		{
			return false;
		}
		
		long vitality = effected.getActingPlayer().getVitalityPoints();
		vitality += _amount;
		if (vitality >= PcStat.MAX_VITALITY_POINTS)
		{
			vitality = PcStat.MAX_VITALITY_POINTS;
		}
		effected.getActingPlayer().setVitalityPoints((int) vitality, true);
		
		return skill.isToggle();
	}
	
	@Override
	public void onExit(L2Character effector, L2Character effected, Skill skill)
	{
		if ((effected != null) //
			&& effected.isPlayer())
		{
			final BuffInfo info = effected.getEffectList().getBuffInfoBySkillId(skill.getId());
			if ((info != null) && !info.isRemoved())
			{
				long vitality = effected.getActingPlayer().getVitalityPoints();
				vitality += _amount * 100;
				if (vitality >= PcStat.MAX_VITALITY_POINTS)
				{
					vitality = PcStat.MAX_VITALITY_POINTS;
				}
				effected.getActingPlayer().setVitalityPoints((int) vitality, true);
			}
		}
	}
}
