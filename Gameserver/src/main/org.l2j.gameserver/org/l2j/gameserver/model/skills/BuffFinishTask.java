/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model.skills;

import org.l2j.commons.threading.ThreadPool;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Mobius
 */
public class BuffFinishTask
{
	private final Map<BuffInfo, AtomicInteger> _buffInfos = new ConcurrentHashMap<>();
	private final ScheduledFuture<?> _task = ThreadPool.scheduleAtFixedRate(() ->
	{
		for (Entry<BuffInfo, AtomicInteger> entry : _buffInfos.entrySet())
		{
			final BuffInfo info = entry.getKey();
			if ((info.getEffected() != null) && (entry.getValue().incrementAndGet() > info.getAbnormalTime()))
			{
				info.getEffected().getEffectList().stopSkillEffects(false, info.getSkill().getId());
			}
		}
	}, 0, 1000);
	
	public ScheduledFuture<?> getTask()
	{
		return _task;
	}
	
	public void addBuffInfo(BuffInfo info)
	{
		_buffInfos.put(info, new AtomicInteger());
	}
	
	public void removeBuffInfo(BuffInfo info)
	{
		_buffInfos.remove(info);
	}
}
