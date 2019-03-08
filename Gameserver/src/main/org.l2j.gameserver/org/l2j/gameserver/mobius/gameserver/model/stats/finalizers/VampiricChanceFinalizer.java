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
package org.l2j.gameserver.mobius.gameserver.model.stats.finalizers;

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.stats.IStatsFunction;
import org.l2j.gameserver.mobius.gameserver.model.stats.Stats;

import java.util.Optional;

/**
 * @author Sdw
 */
public class VampiricChanceFinalizer implements IStatsFunction
{
	@Override
	public double calc(L2Character creature, Optional<Double> base, Stats stat)
	{
		throwIfPresent(base);
		
		final double amount = creature.getStat().getValue(Stats.ABSORB_DAMAGE_PERCENT, 0) * 100;
		final double vampiricSum = creature.getStat().getVampiricSum();
		
		return amount > 0 ? Stats.defaultValue(creature, stat, Math.min(1.0, vampiricSum / amount / 100)) : 0;
	}
}
