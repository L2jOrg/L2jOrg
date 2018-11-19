package l2s.commons.lang.reference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Вспомогательный класс для работы с HardReference
 * 
 * @author G1ta0
 */
public class HardReferences
{
	private HardReferences(){}
	
	private static class EmptyReferencedHolder extends AbstractHardReference<Object>
	{		
		public EmptyReferencedHolder(Object reference)
		{
			super(reference);
		}
	}
	
	private static HardReference<?> EMPTY_REF = new EmptyReferencedHolder(null);
	
	@SuppressWarnings("unchecked")
	public static <T> HardReference<T> emptyRef()
	{
		return (HardReference<T>)EMPTY_REF;
	}
	
	/**
	 * Получить список объектов, исходя из коллекции ссылок. Нулевые ссылки будут отфильтрованы.
	 * 
	 * @param <T>
	 * @param refs коллекция ссылок
	 * @return коллекцию объектов, на которые указываю ссылки
	 */
	public static <T> Collection<T> unwrap(Collection<HardReference<T>> refs)
	{
		List<T> result = new ArrayList<T>(refs.size());
		for(HardReference<T> ref : refs)
		{
			T obj = ref.get();
			if(obj != null)
				result.add(obj);
		}
		return result;
	}
	
	private static class WrappedIterable<T> implements Iterable<T>
	{
		final Iterable<HardReference<T>> refs;
		WrappedIterable(Iterable<HardReference<T>> refs)
		{
			this.refs = refs;
		}
		
		private static class WrappedIterator<T> implements Iterator<T>
		{
			final Iterator<HardReference<T>> iterator;
			
			WrappedIterator(Iterator<HardReference<T>> iterator)
			{
				this.iterator = iterator;
			}
			
			@Override
			public boolean hasNext()
			{
				return iterator.hasNext();
			}

			@Override
			public T next()
			{
				return iterator.next().get();
			}

			@Override
			public void remove()
			{
				iterator.remove();
			}
		}
		
		@Override
		public Iterator<T> iterator()
		{
			return new WrappedIterator<T>(refs.iterator());
		}
	}
	
	/**
	 * Итерация по коллекции ссылок на объекты.
	 * 
	 * @param <T>
	 * @param refs коллекция ссылок на объекты
	 * @return враппер, который будет возвращать при итерации объекты, на которые указывают ссылки
	 */
	public static <T> Iterable<T> iterate(Iterable<HardReference<T>> refs)
	{
		return new WrappedIterable<T>(refs);
	}
}
