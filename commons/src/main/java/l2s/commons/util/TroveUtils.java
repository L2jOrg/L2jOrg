package l2s.commons.util;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * Author: VISTALL
 * Date:  21:59/15.12.2010
 */
public class TroveUtils
{
	@SuppressWarnings("rawtypes")
	private static final TIntObjectHashMap EMPTY_INT_OBJECT_MAP = new TIntObjectHashMapEmpty();
	public static final TIntArrayList EMPTY_INT_ARRAY_LIST = new TIntArrayListEmpty();
	
	@SuppressWarnings("unchecked")
	public static <V> TIntObjectHashMap<V> emptyIntObjectMap()
	{
		return (TIntObjectHashMap<V>)EMPTY_INT_OBJECT_MAP;
	}

	private static class TIntObjectHashMapEmpty<V> extends TIntObjectHashMap<V>
	{
		TIntObjectHashMapEmpty()
		{
			super(0);
		}

		@Override
		public V put(int key, V value)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public V putIfAbsent(int key, V value)
		{
			throw new UnsupportedOperationException();
		}
	}
	
	private static class TIntArrayListEmpty extends TIntArrayList
	{
		TIntArrayListEmpty()
		{
			super(0);
		}

		@Override
		public boolean add(int val)
		{
			throw new UnsupportedOperationException();
		}
	}
}
