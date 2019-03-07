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
package org.l2j.gameserver.mobius.gameserver.instancemanager;

import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.events.timers.TimerHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author UnAfraid
 */
public class TimersManager
{
	private final Map<Integer, List<TimerHolder<?>>> _timers = new ConcurrentHashMap<>();
	
	public void registerTimer(TimerHolder<?> timer)
	{
		final L2Npc npc = timer.getNpc();
		if (npc != null)
		{
			final List<TimerHolder<?>> npcTimers = _timers.computeIfAbsent(npc.getObjectId(), key -> new ArrayList<>());
			synchronized (npcTimers)
			{
				npcTimers.add(timer);
			}
		}
		
		final L2PcInstance player = timer.getPlayer();
		if (player != null)
		{
			final List<TimerHolder<?>> playerTimers = _timers.computeIfAbsent(player.getObjectId(), key -> new ArrayList<>());
			synchronized (playerTimers)
			{
				playerTimers.add(timer);
			}
		}
	}
	
	public void cancelTimers(int objectId)
	{
		final List<TimerHolder<?>> timers = _timers.remove(objectId);
		if (timers != null)
		{
			synchronized (timers)
			{
				timers.forEach(TimerHolder::cancelTimer);
			}
		}
	}
	
	public void unregisterTimer(int objectId, TimerHolder<?> timer)
	{
		final List<TimerHolder<?>> timers = _timers.get(objectId);
		if (timers != null)
		{
			synchronized (timers)
			{
				timers.remove(timer);
			}
		}
	}
	
	public static TimersManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final TimersManager _instance = new TimersManager();
	}
}
