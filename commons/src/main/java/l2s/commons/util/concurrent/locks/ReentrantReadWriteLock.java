package l2s.commons.util.concurrent.locks;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * Класс обеспечивающий блокировку с повторным входом и разделением на чтение/запись. Упрощенный аналог {@link java.util.concurrent.locks.ReentrantReadWriteLock}
 * Потребляет меньше памяти, менее производителен.
 * <p>
 * Возможность повторого входа также обеспечивает понижение блокировки с записи на чтение,
 * для этого необходимо удерживая блокировку на запись, получить блокировку на чтение и освободить блокировку на запись.
 * Следует помнить, что повышение блокировки на запись, при удержании блокировки на чтение, невозможно.
 * </p>
 * @author G1ta0
 */
public class ReentrantReadWriteLock
{
	private static final AtomicIntegerFieldUpdater<ReentrantReadWriteLock> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(ReentrantReadWriteLock.class, "state");

	static final int SHARED_SHIFT = 16;
	static final int SHARED_UNIT = (1 << SHARED_SHIFT);
	static final int MAX_COUNT = (1 << SHARED_SHIFT) - 1;
	static final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;

	/** Returns the number of shared holds represented in count  */
	static int sharedCount(int c)
	{
		return c >>> SHARED_SHIFT;
	}

	/** Returns the number of exclusive holds represented in count  */
	static int exclusiveCount(int c)
	{
		return c & EXCLUSIVE_MASK;
	}

	/**
	 * A counter for per-thread read hold counts.
	 * Maintained as a ThreadLocal; cached in cachedHoldCounter
	 */
	static final class HoldCounter
	{
		int count;
		// Use id, not reference, to avoid garbage retention
		final long tid = Thread.currentThread().getId();

		/** Decrement if positive; return previous value */
		int tryDecrement()
		{
			int c = count;
			if(c > 0)
				count = c - 1;
			return c;
		}
	}

	/**
	 * ThreadLocal subclass. Easiest to explicitly define for sake
	 * of deserialization mechanics.
	 */
	static final class ThreadLocalHoldCounter extends ThreadLocal<HoldCounter>
	{
		public HoldCounter initialValue()
		{
			return new HoldCounter();
		}
	}

	/**
	 * The number of read locks held by current thread.
	 * Initialized only in constructor and readObject.
	 */
	transient ThreadLocalHoldCounter readHolds;

	/**
	 * The hold count of the last thread to successfully acquire
	 * readLock. This saves ThreadLocal lookup in the common case
	 * where the next thread to release is the last one to
	 * acquire. This is non-volatile since it is just used
	 * as a heuristic, and would be great for threads to cache.
	 */
	transient HoldCounter cachedHoldCounter;

	private Thread owner;
	private volatile int state;

	public ReentrantReadWriteLock()
	{
		readHolds = new ThreadLocalHoldCounter();
		setState(0);
	}

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

	public void writeLock()
	{
		Thread current = Thread.currentThread();
		for(;;)
		{
			int c = getState();
			int w = exclusiveCount(c);
			if(c != 0)
			{
				// (Note: if c != 0 and w == 0 then shared count != 0)
				if(w == 0 || current != getExclusiveOwnerThread())
					continue;
				if(w + exclusiveCount(1) > MAX_COUNT)
					throw new Error("Maximum lock count exceeded");
			}
			if(compareAndSetState(c, c + 1))
			{
				setExclusiveOwnerThread(current);
				return;
			}
		}
	}

	public boolean tryWriteLock()
	{
		Thread current = Thread.currentThread();
		int c = getState();
		if(c != 0)
		{
			int w = exclusiveCount(c);
			if(w == 0 || current != getExclusiveOwnerThread())
				return false;
			if(w == MAX_COUNT)
				throw new Error("Maximum lock count exceeded");
		}
		if(!compareAndSetState(c, c + 1))
			return false;
		setExclusiveOwnerThread(current);
		return true;
	}

	final boolean tryReadLock()
	{
		Thread current = Thread.currentThread();
		int c = getState();
		int w = exclusiveCount(c);
		if(w != 0 && getExclusiveOwnerThread() != current)
			return false;
		if(sharedCount(c) == MAX_COUNT)
			throw new Error("Maximum lock count exceeded");
		if(compareAndSetState(c, c + SHARED_UNIT))
		{
			HoldCounter rh = cachedHoldCounter;
			if(rh == null || rh.tid != current.getId())
				cachedHoldCounter = rh = readHolds.get();
			rh.count++;
			return true;
		}
		return false;
	}

	public void readLock()
	{
		Thread current = Thread.currentThread();
		HoldCounter rh = cachedHoldCounter;
		if(rh == null || rh.tid != current.getId())
			rh = readHolds.get();
		for(;;)
		{
			int c = getState();
			int w = exclusiveCount(c);
			if(w != 0 && getExclusiveOwnerThread() != current)
				continue;
			if(sharedCount(c) == MAX_COUNT)
				throw new Error("Maximum lock count exceeded");
			if(compareAndSetState(c, c + SHARED_UNIT))
			{
				cachedHoldCounter = rh; // cache for release
				rh.count++;
				return;
			}
		}
	}

	public void writeUnlock()
	{
		int nextc = getState() - 1;
		if(Thread.currentThread() != getExclusiveOwnerThread())
			throw new IllegalMonitorStateException();
		if(exclusiveCount(nextc) == 0)
		{
			setExclusiveOwnerThread(null);
			setState(nextc);
			return;
		}
		else
		{
			setState(nextc);
			return;
		}
	}

	public void readUnlock()
	{
		HoldCounter rh = cachedHoldCounter;
		Thread current = Thread.currentThread();
		if(rh == null || rh.tid != current.getId())
			rh = readHolds.get();
		if(rh.tryDecrement() <= 0)
			throw new IllegalMonitorStateException();
		for(;;)
		{
			int c = getState();
			int nextc = c - SHARED_UNIT;
			if(compareAndSetState(c, nextc))
				return;
		}
	}
}
