package org.l2j.commons.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.*;
import java.time.Duration;

/**
 * Thread to check for deadlocked threads.
 * @author -Nemesiss- L2M
 */
public class DeadLockDetector extends Thread
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DeadLockDetector.class);
	
	private final Duration _checkInterval;
	private final Runnable _deadLockCallback;
	private final ThreadMXBean tmx;
	
	public DeadLockDetector(Duration checkInterval, Runnable deadLockCallback)
	{
		super("DeadLockDetector");
		_checkInterval = checkInterval;
		_deadLockCallback = deadLockCallback;
		tmx = ManagementFactory.getThreadMXBean();
		setDaemon(true);
	}
	
	@Override
	public final void run()
	{
		boolean deadlock = false;
		while (!deadlock)
		{
			try
			{
				final long[] ids = tmx.findDeadlockedThreads();
				
				if (ids != null)
				{
					deadlock = true;
					final ThreadInfo[] tis = tmx.getThreadInfo(ids, true, true);
					final StringBuilder info = new StringBuilder();
					info.append("DeadLock Found!\n");
					for (ThreadInfo ti : tis)
					{
						info.append(ti.toString());
					}
					
					for (ThreadInfo ti : tis)
					{
						final LockInfo[] locks = ti.getLockedSynchronizers();
						final MonitorInfo[] monitors = ti.getLockedMonitors();
						if ((locks.length == 0) && (monitors.length == 0))
						{
							continue;
						}
						
						ThreadInfo dl = ti;
						info.append("Java-level deadlock:\n");
						info.append('\t');
						info.append(dl.getThreadName());
						info.append(" is waiting to lock ");
						info.append(dl.getLockInfo().toString());
						info.append(" which is held by ");
						info.append(dl.getLockOwnerName());
						info.append("\n");
						while ((dl = tmx.getThreadInfo(new long[]
						{
							dl.getLockOwnerId()
						}, true, true)[0]).getThreadId() != ti.getThreadId())
						{
							info.append('\t');
							info.append(dl.getThreadName());
							info.append(" is waiting to lock ");
							info.append(dl.getLockInfo().toString());
							info.append(" which is held by ");
							info.append(dl.getLockOwnerName());
							info.append("\n");
						}
					}
					
					LOGGER.warn(info.toString());
					
					if (_deadLockCallback != null)
					{
						_deadLockCallback.run();
					}
				}
				Thread.sleep(_checkInterval.toMillis());
			}
			catch (Exception e)
			{
				LOGGER.warn("DeadLockDetector: ", e);
			}
		}
	}
}
