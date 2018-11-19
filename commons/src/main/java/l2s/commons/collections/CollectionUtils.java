package l2s.commons.collections;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public final class CollectionUtils
{
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