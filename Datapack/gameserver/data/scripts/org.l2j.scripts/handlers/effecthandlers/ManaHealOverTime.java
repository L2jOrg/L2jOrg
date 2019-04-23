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

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Stats;

/**
 * Mana Heal Over Time effect implementation.
 */
public final class ManaHealOverTime extends AbstractEffect
{
	private final double _power;
	
	public ManaHealOverTime(StatsSet params)
	{
		_power = params.getDouble("power", 0);
		setTicks(params.getInt("ticks"));
	}
	
	@Override
	public boolean onActionTime(L2Character effector, L2Character effected, Skill skill, L2ItemInstance item)
	{
		if (effected.isDead())
		{
			return false;
		}
		
		double mp = effected.getCurrentMp();
		final double maxmp = effected.getMaxRecoverableMp();
		
		// Not needed to set the MP and send update packet if player is already at max MP
		if (mp >= maxmp)
		{
			return true;
		}

		double power = _power;
		if ((item != null) && (item.isPotion() || item.isElixir()))
		{
			power += effected.getStat().getValue(Stats.ADDITIONAL_POTION_MP, 0) / getTicks();
		}

		mp += power * getTicksMultiplier();
		mp = Math.min(mp, maxmp);
		effected.setCurrentMp(mp, false);
		effected.broadcastStatusUpdate(effector);
		return skill.isToggle();
	}
}
