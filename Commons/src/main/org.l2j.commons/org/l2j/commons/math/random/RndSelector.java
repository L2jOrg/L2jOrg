package org.l2j.commons.math.random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.l2j.commons.util.Rnd;

public class RndSelector<E>
{
	protected class RndNode<T> implements Comparable<RndNode<T>>
	{
		private final T value;
		private final int weight;

		public RndNode(T value, int weight)
		{
			this.value = value;
			this.weight = weight;
		}

		@Override
		public int compareTo(RndNode<T> o)
		{
			return this.weight - weight;
		}
	}

	private int totalWeight = 0;
	protected final List<RndNode<E>> nodes;

	public RndSelector(boolean concurrent)
	{
		nodes = concurrent ? new CopyOnWriteArrayList<RndNode<E>>() : new ArrayList<RndNode<E>>();
	}

	public RndSelector()
	{
		this(false);
	}

	public RndSelector(int initialCapacity)
	{
		nodes = new ArrayList<RndNode<E>>(initialCapacity);
	}

	public void add(E value, int weight)
	{
		if(value == null || weight <= 0)
			return;
		totalWeight += weight;
		nodes.add(new RndNode<E>(value, weight));
	}

	/**
	 * Вернет один из елементов или null, null возможен только если сумма весов всех элементов меньше maxWeight
	 */
	public E chance(int maxWeight)
	{
		if(maxWeight <= 0)
			return null;

		Collections.sort(nodes);

		int r = Rnd.get(maxWeight);
		int weight = 0;
		for(int i = 0; i < nodes.size(); i++)
			if((weight += nodes.get(i).weight) > r)
				return nodes.get(i).value;
		return null;
	}

	/**
	 * Вернет один из елементов или null, null возможен только если сумма весов всех элементов меньше 100
	 */
	public E chance()
	{
		return chance(100);
	}

	/**
	 * Вернет один из елементов
	 */
	public E select()
	{
		return chance(totalWeight);
	}

	public void clear()
	{
		totalWeight = 0;
		nodes.clear();
	}
}