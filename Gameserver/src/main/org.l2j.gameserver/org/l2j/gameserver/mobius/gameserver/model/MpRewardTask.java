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

import com.l2jmobius.Config;
import org.l2j.commons.concurrent.ThreadPool;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.mobius.gameserver.model.actor.templates.L2NpcTemplate;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author UnAfraid
 */
public class MpRewardTask
{
	private final AtomicInteger _count;
	private final double _value;
	private final ScheduledFuture<?> _task;
	private final L2Character _creature;
	
	public MpRewardTask(L2Character creature, L2Npc npc)
	{
		final L2NpcTemplate template = npc.getTemplate();
		_creature = creature;
		_count = new AtomicInteger(template.getMpRewardTicks());
		_value = calculateBaseValue(npc, creature);
		_task = ThreadPoolManager.getInstance().scheduleAtFixedRate(this::run, Config.EFFECT_TICK_RATIO, Config.EFFECT_TICK_RATIO);
	}
	
	/**
	 * @param npc
	 * @param creature
	 * @return
	 */
	private double calculateBaseValue(L2Npc npc, L2Character creature)
	{
		final L2NpcTemplate template = npc.getTemplate();
		switch (template.getMpRewardType())
		{
			case PER:
			{
				return (creature.getMaxMp() * (template.getMpRewardValue() / 100)) / template.getMpRewardTicks();
			}
		}
		return template.getMpRewardValue() / template.getMpRewardTicks();
	}
	
	private void run()
	{
		if ((_count.decrementAndGet() <= 0) || (_creature.isPlayer() && !_creature.getActingPlayer().isOnline()))
		{
			_task.cancel(false);
			return;
		}
		
		_creature.setCurrentMp(_creature.getCurrentMp() + _value);
	}
}
