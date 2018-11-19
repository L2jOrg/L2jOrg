package l2s.commons.util.concurrent.locks;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * Класс обеспечивающий блокировку с повторным входом. Упрощенный аналог {@link java.util.concurrent.locks.ReentrantLock}
 * Потребляет меньше памяти, менее производителен.
 * 
 * @author G1ta0
 */
public class ReentrantLock
{
	private static final AtomicIntegerFieldUpdater<ReentrantLock> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(ReentrantLock.class, "state");

	private Thread owner;
	private volatile int state;

	public ReentrantLock()
	{}

	private final int getState()
	{
		return state;
	}

	private void setState(int newState)
	{
		state = newState;
	}

	private boolean compareAndSetState(int expect, int update)
	{
		return stateUpdater.compareAndSet(this, expect, update);
	}

	private Thread getExclusiveOwnerThread()
	{
		return owner;
	}

	private void setExclusiveOwnerThread(Thread thread)
	{
		owner = thread;
	}

	public void lock()
	{
		if(compareAndSetState(0, 1))
			setExclusiveOwnerThread(Thread.currentThread());
		else
			for(;;)
			{
				if(tryLock())
					break;
			}
	}

	public boolean tryLock()
	{
		final Thread current = Thread.currentThread();
		int c = getState();
		if(c == 0)
		{
			if(compareAndSetState(0, 1))
			{
				setExclusiveOwnerThread(current);
				return true;
			}
		}
		else if(current == getExclusiveOwnerThread())
		{
			int nextc = c + 1;
			if(nextc < 0)
				throw new Error("Maximum lock count exceeded");
			setState(nextc);
			return true;
		}
		return false;
	}

	public boolean unlock()
	{
		int c = getState() - 1;
		if(Thread.currentThread() != getExclusiveOwnerThread())
			throw new IllegalMonitorStateException();
		boolean free = false;
		if(c == 0)
		{
			free = true;
			setExclusiveOwnerThread(null);
		}
		setState(c);
		return free;
	}
}
