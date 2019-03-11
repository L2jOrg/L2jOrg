package org.l2j.gameserver;

import org.l2j.commons.threading.PriorityThreadFactory;
import org.l2j.commons.threading.RejectedExecutionHandlerImpl;
import org.l2j.commons.threading.RunnableStatsWrapper;

import java.util.concurrent.*;

public class ThreadPoolManager
{
	private static final long MAX_DELAY = TimeUnit.NANOSECONDS.toMillis(Long.MAX_VALUE - System.nanoTime()) / 2;

	private static final ThreadPoolManager _instance = new ThreadPoolManager();

	public static ThreadPoolManager getInstance() {
		return _instance;
	}

	private final ScheduledThreadPoolExecutor _scheduledExecutor;
	private final ThreadPoolExecutor _executor;

	private boolean _shutdown;

	private ThreadPoolManager() {
		RejectedExecutionHandler rejectedHandler = new RejectedExecutionHandlerImpl();

		_scheduledExecutor = new ScheduledThreadPoolExecutor(Config.SCHEDULED_THREAD_POOL_SIZE, new PriorityThreadFactory("ScheduledThreadPool", Thread.NORM_PRIORITY), new ThreadPoolExecutor.CallerRunsPolicy());
		_scheduledExecutor.setRejectedExecutionHandler(rejectedHandler);

		_executor = new ThreadPoolExecutor(Config.EXECUTOR_THREAD_POOL_SIZE, Integer.MAX_VALUE, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new PriorityThreadFactory("ThreadPoolExecutor", Thread.NORM_PRIORITY), new ThreadPoolExecutor.CallerRunsPolicy());
		_executor.setRejectedExecutionHandler(rejectedHandler);

		scheduleAtFixedRate(() -> { _scheduledExecutor.purge(); _executor.purge();  }, 300000L, 300000L);
	}

	private long validate(long delay)
	{
        long validatedDelay = Math.max(0, Math.min(ThreadPoolManager.MAX_DELAY, delay));
        if (delay > validatedDelay)
            return -1;

        long secondsToRestart = Shutdown.getInstance().getSeconds() * 1000L;
        if(secondsToRestart > 0 && validatedDelay > secondsToRestart)
            return -1;

        return validatedDelay;
	}

	public boolean isShutdown()
	{
		return _shutdown;
	}

	public static Runnable wrap(Runnable r)
	{
		return Config.ENABLE_RUNNABLE_STATS ? RunnableStatsWrapper.wrap(r) : r;
	}

	public ScheduledFuture<?> schedule(Runnable r, long delay)
	{
        delay = validate(delay);
        if(delay == -1)
            return null;

        return _scheduledExecutor.schedule(wrap(r), delay, TimeUnit.MILLISECONDS);
	}

	public ScheduledFuture<?> scheduleAtFixedRate(Runnable r, long initial, long delay)
	{
        initial = validate(initial);
        if(initial == -1)
            return null;

        delay = validate(delay);
        if(delay == -1)
            return _scheduledExecutor.schedule(wrap(r), initial, TimeUnit.MILLISECONDS);

        return _scheduledExecutor.scheduleAtFixedRate(wrap(r), initial, delay, TimeUnit.MILLISECONDS);
	}

	public ScheduledFuture<?> scheduleAtFixedDelay(Runnable r, long initial, long delay)
	{
        initial = validate(initial);
        if(initial == -1)
            return null;

        delay = validate(delay);
        if(delay == -1)
            return _scheduledExecutor.schedule(wrap(r), initial, TimeUnit.MILLISECONDS);

        return _scheduledExecutor.scheduleWithFixedDelay(wrap(r), initial, delay, TimeUnit.MILLISECONDS);
	}

	public static void execute(Runnable r) {
		getInstance()._executor.execute(wrap(r));
	}

	public void shutdown() throws InterruptedException
	{
		_shutdown = true;
		try
		{
			_scheduledExecutor.shutdown();
			_scheduledExecutor.awaitTermination(10, TimeUnit.SECONDS);
		}
		finally
		{
			_executor.shutdown();
			_executor.awaitTermination(1, TimeUnit.MINUTES);
		}
	}

	public CharSequence getStats()
	{
		StringBuilder list = new StringBuilder();

		list.append("ScheduledThreadPool\n");
		list.append("=================================================\n");
		list.append("\tgetActiveCount: ...... ").append(_scheduledExecutor.getActiveCount()).append("\n");
		list.append("\tgetCorePoolSize: ..... ").append(_scheduledExecutor.getCorePoolSize()).append("\n");
		list.append("\tgetPoolSize: ......... ").append(_scheduledExecutor.getPoolSize()).append("\n");
		list.append("\tgetLargestPoolSize: .. ").append(_scheduledExecutor.getLargestPoolSize()).append("\n");
		list.append("\tgetMaximumPoolSize: .. ").append(_scheduledExecutor.getMaximumPoolSize()).append("\n");
		list.append("\tgetCompletedTaskCount: ").append(_scheduledExecutor.getCompletedTaskCount()).append("\n");
		list.append("\tgetQueuedTaskCount: .. ").append(_scheduledExecutor.getQueue().size()).append("\n");
		list.append("\tgetTaskCount: ........ ").append(_scheduledExecutor.getTaskCount()).append("\n");
		list.append("ThreadPoolExecutor\n");
		list.append("=================================================\n");
		list.append("\tgetActiveCount: ...... ").append(_executor.getActiveCount()).append("\n");
		list.append("\tgetCorePoolSize: ..... ").append(_executor.getCorePoolSize()).append("\n");
		list.append("\tgetPoolSize: ......... ").append(_executor.getPoolSize()).append("\n");
		list.append("\tgetLargestPoolSize: .. ").append(_executor.getLargestPoolSize()).append("\n");
		list.append("\tgetMaximumPoolSize: .. ").append(_executor.getMaximumPoolSize()).append("\n");
		list.append("\tgetCompletedTaskCount: ").append(_executor.getCompletedTaskCount()).append("\n");
		list.append("\tgetQueuedTaskCount: .. ").append(_executor.getQueue().size()).append("\n");
		list.append("\tgetTaskCount: ........ ").append(_executor.getTaskCount()).append("\n");

		return list;
	}
}