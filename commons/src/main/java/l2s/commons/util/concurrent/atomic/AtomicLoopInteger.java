package l2s.commons.util.concurrent.atomic;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * Атомарный цикличный Integer для диапазона значений.
 *
 * @author G1ta0
 */
public class AtomicLoopInteger
{
	private static final AtomicIntegerFieldUpdater<AtomicLoopInteger> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(AtomicLoopInteger.class, "value");

	private volatile int value;
	private final int min, max;

	/**
	 * Создаем цикличный Integer в пределах min..max, начальное значение initialValue.
	 * 
	 * @param min миникальное значение переменной цикла
	 * @param max максимальное значение переменной цикла
	 */
	public AtomicLoopInteger(int initialValue, int min, int max)
	{
		this.min = min;
		this.max = max;
		value = initialValue;
	}

	public int get()
	{
		return value;
	}

	public final int incrementAndGet()
	{
		int result;
		for(;;)
		{
			if((result = stateUpdater.get(this)) < max)
			{
				if(stateUpdater.compareAndSet(this, result, result + 1))
					return result + 1;
			}
			else if(stateUpdater.compareAndSet(this, max, min))
				return min;
		}
	}

	public final int getAndIncrement()
	{
		int result;
		for(;;)
		{
			if((result = stateUpdater.get(this)) < max)
			{
				if(stateUpdater.compareAndSet(this, result, result + 1))
					return result;
			}
			else if(stateUpdater.compareAndSet(this, max, min))
				return max;
		}
	}

	public final int decrementAndGet()
	{
		int result;
		for(;;)
		{
			if((result = stateUpdater.get(this)) > min)
			{
				if(stateUpdater.compareAndSet(this, result, result - 1))
					return result - 1;
			}
			else if(stateUpdater.compareAndSet(this, min, max))
				return max;
		}
	}

	public final int getAndDecrement()
	{
		int result;
		for(;;)
		{
			if((result = stateUpdater.get(this)) > min)
			{
				if(stateUpdater.compareAndSet(this, result, result - 1))
					return result;
			}
			else if(stateUpdater.compareAndSet(this, min, max))
				return min;
		}
	}
}
