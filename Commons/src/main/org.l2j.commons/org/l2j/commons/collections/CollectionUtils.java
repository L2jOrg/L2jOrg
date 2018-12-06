package org.l2j.commons.collections;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.util.Objects.nonNull;

public final class CollectionUtils {
	private static final int POOL_MAX_SIZE = 20;
	private static final Queue<List> pooledListQueue = new ConcurrentLinkedQueue<>();

	@SuppressWarnings("unchecked")
	public static<E> List<E> pooledList() {
		var list = pooledListQueue.poll();
		return (List<E>) (nonNull(list) ?  list : new ArrayList());
	}


	public static void recycle(List list) {
		if(pooledListQueue.size() < POOL_MAX_SIZE) {
			list.clear();
			pooledListQueue.add(list);
		}
	}
	/**
	 * copy from {@link java.util.AbstractList}
	 * @param collection
	 * @param <E>
	 * @return hash
	 */
	public static <E> int hashCode(Collection<E> collection)
	{
		int hashCode = 1;
		Iterator<E> i = collection.iterator();
		while (i.hasNext())
		{
			E obj = i.next();
			hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode());
		}
		return hashCode;
	}

	public static <E> E safeGet(List<E> list, int index)
	{
		return list.size() > index ? list.get(index) : null;
	}
}