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
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Stats;

/**
 * Cp Heal Over Time effect implementation.
 */
public final class CpHealOverTime extends AbstractEffect
{
	private final double _power;
	
	public CpHealOverTime(StatsSet params)
	{
		_power = params.getDouble("power", 0);
		setTicks(params.getInt("ticks"));
	}
	
	@Override
	public boolean onActionTime(Creature effector, Creature effected, Skill skill, Item item)
	{
		if (effected.isDead())
		{
			return false;
		}
		
		double cp = effected.getCurrentCp();
		final double maxcp = effected.getMaxRecoverableCp();
		
		// Not needed to set the CP and send update packet if player is already at max CP
		if (cp >= maxcp)
		{
			return false;
		}

		double power = _power;
		if ((item != null) && (item.isPotion() || item.isElixir()))
		{
			power += effected.getStat().getValue(Stats.ADDITIONAL_POTION_CP, 0) / getTicks();
		}

		cp += power * getTicksMultiplier();
		cp = Math.min(cp, maxcp);
		effected.setCurrentCp(cp, false);
		effected.broadcastStatusUpdate(effector);
		return true;
	}
}
