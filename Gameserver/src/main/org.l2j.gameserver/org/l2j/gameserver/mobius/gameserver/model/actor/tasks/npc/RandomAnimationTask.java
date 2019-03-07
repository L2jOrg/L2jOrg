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
package org.l2j.gameserver.mobius.gameserver.model.actor.tasks.npc;

import com.l2jmobius.Config;
import com.l2jmobius.commons.concurrent.ThreadPool;
import com.l2jmobius.commons.util.Rnd;
import com.l2jmobius.gameserver.model.actor.L2Npc;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.l2jmobius.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;

/**
 * @author Nik
 */
public class RandomAnimationTask implements Runnable
{
	private static final Logger LOGGER = Logger.getLogger(RandomAnimationTask.class.getName());
	private final L2Npc _npc;
	private boolean _stopTask;
	
	public RandomAnimationTask(L2Npc npc)
	{
		_npc = npc;
	}
	
	@Override
	public void run()
	{
		if (_stopTask)
		{
			return;
		}
		
		try
		{
			if (!_npc.isInActiveRegion())
			{
				return;
			}
			
			// Cancel further animation timers until intention is changed to ACTIVE again.
			if (_npc.isAttackable() && (_npc.getAI().getIntention() != AI_INTENTION_ACTIVE))
			{
				return;
			}
			
			if (!_npc.isDead() && !_npc.hasBlockActions())
			{
				_npc.onRandomAnimation(Rnd.get(2, 3));
			}
			
			startRandomAnimationTimer();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Execution of RandomAnimationTask has failed.", e);
		}
	}
	
	/**
	 * Create a RandomAnimation Task that will be launched after the calculated delay.
	 */
	public void startRandomAnimationTimer()
	{
		if (!_npc.hasRandomAnimation() || _stopTask)
		{
			return;
		}
		
		final int minWait = _npc.isAttackable() ? Config.MIN_MONSTER_ANIMATION : Config.MIN_NPC_ANIMATION;
		final int maxWait = _npc.isAttackable() ? Config.MAX_MONSTER_ANIMATION : Config.MAX_NPC_ANIMATION;
		
		// Calculate the delay before the next animation
		final int interval = Rnd.get(minWait, maxWait) * 1000;
		
		// Create a RandomAnimation Task that will be launched after the calculated delay
		ThreadPool.schedule(this, interval);
	}
	
	/**
	 * Stops the task from continuing and blocks it from continuing ever again. You need to create new task if you want to start it again.
	 */
	public void stopRandomAnimationTimer()
	{
		_stopTask = true;
	}
}
