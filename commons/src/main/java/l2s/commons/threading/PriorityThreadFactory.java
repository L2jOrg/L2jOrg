package l2s.commons.threading;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PriorityThreadFactory implements ThreadFactory
{
	private static final Logger _log = LoggerFactory.getLogger(PriorityThreadFactory.class);

	private int _prio;
	private String _name;
	private AtomicInteger _threadNumber = new AtomicInteger(1);
	private ThreadGroup _group;

	public PriorityThreadFactory(String name, int prio)
	{
		_prio = prio;
		_name = name;
		_group = new ThreadGroup(_name);
	}

	@Override
	public Thread newThread(Runnable r)
	{
		Thread t = new Thread(_group, r)
		{
			@Override
			public void run()
			{
				try
				{
					super.run();
				}
				catch(Exception e)
				{
					_log.error("Exception: " + e, e);
				}
			}
		};
		t.setName(_name + "-" + _threadNumber.getAndIncrement());
		t.setPriority(_prio);
		return t;
	}

	public ThreadGroup getGroup()
	{
		return _group;
	}
}
