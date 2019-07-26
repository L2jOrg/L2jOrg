package org.l2j.gameserver.model.skills;

import org.l2j.commons.threading.ThreadPoolManager;

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
	private final ScheduledFuture<?> _task = ThreadPoolManager.scheduleAtFixedRate(() ->
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
