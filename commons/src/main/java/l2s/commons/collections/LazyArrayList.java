package l2s.commons.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * Упрощенная реализация массива с интерфейсом <tt>List</tt>
 * Не потоко-безопасная версия. Порядок элементов при удалении не сохраняется. В качестве аргумента при добавлении элементов может быть <tt>null</tt>.
 * <p>
 * В качестве параметра при создании обьекта <tt>LazyArrayList</tt> задается начальный размер массива элементов <tt>initialCapacity</tt>.
 * </p>
 * При добавлении элементов, в случае переполнения массива, размер увеличивается на <tt>capacity * 1.5</tt>
 * При удалении элемента, массив не сокращается, вместо этого последний элемент массива становится на место удаленного.
 * <p>
 * Для идентификации элементов используется <tt>==</tt>, а не {@link Object#equals(Object) Object.equals(Object)}.
 * </p>
 * @author G1ta0
 */
@SuppressWarnings("unchecked")
public class LazyArrayList<E> implements List<E>, RandomAccess, Cloneable, java.io.Serializable
{
	private static final long serialVersionUID = 8683452581122892189L;

	@SuppressWarnings("rawtypes")
	private static class PoolableLazyArrayListFactory implements PoolableObjectFactory
	{
		@Override
		public Object makeObject() throws Exception
		{
			return new LazyArrayList();
		}

		@Override
		public void destroyObject(Object obj) throws Exception
		{
			((LazyArrayList) obj).clear();
		}

		@Override
		public boolean validateObject(Object obj)
		{
			return true;
		}

		@Override
		public void activateObject(Object obj) throws Exception
		{

		}

		@Override
		public void passivateObject(Object obj) throws Exception
		{
			((LazyArrayList) obj).clear();
		}
	}

	private static final int POOL_SIZE = Integer.parseInt(System.getProperty("lazyarraylist.poolsize", "-1"));
	@SuppressWarnings("rawtypes")
	private static final ObjectPool POOL = new GenericObjectPool(new PoolableLazyArrayListFactory(), POOL_SIZE, GenericObjectPool.WHEN_EXHAUSTED_GROW, 0L, -1);

	/**
	 * Получить список LazyArrayList из пула. В случае, если в пуле нет свободных объектов, будет создан новый.
	 *
	 * @return список LazyArrayList, созданный с параметрами по-умолчанию
	 * @see #recycle
	 */
	public static <E> LazyArrayList<E> newInstance()
	{
		try
		{
			return (LazyArrayList<E>) POOL.borrowObject();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return new LazyArrayList<E>();
	}

	/**
	 * Добавить список LazyArrayList обратно в пул.
	 *
	 * @param obj список LazyArrayList
	 * @see #newInstance
	 */
	public static <E> void recycle(LazyArrayList<E> obj)
	{
		try
		{
			POOL.returnObject(obj);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private static final int L = 1 << 3;
	private static final int H = 1 << 10;

	protected transient Object[] elementData;
	protected transient int size = 0;
	protected transient int capacity = L;

	/**
	 * Создать новый список, с начальным размером внутреннего массива <tt>initialCapacity</tt>
	 * @param initialCapacity начальный размер списка
	 */
	public LazyArrayList(int initialCapacity)
	{
		if (initialCapacity < H)
			while(capacity < initialCapacity)
				capacity <<= 1;
		else
			capacity = initialCapacity;
	}

	public LazyArrayList()
	{
		this(8);
	}

	/**
	 * Добавить элемент в список
	 * @param element элемент, который добавляется в список
	 */
	@Override
	public boolean add(E element)
	{
		ensureCapacity(size + 1);
		elementData[size++] = element;

		return true;
	}

	/**
	 * Заменить элемент списка в заданной позиции
	 * @param index, позиция в которой необходимо заменить элемент
	 * @param element элемент, который следует установить в заданной позиции
	 * @return предыдущий элемент списка в заданной позиции
	 * @throws IndexOutOfBoundsException в случае, если заданная позиция выходит за пределы размерности списка
	 */
	@Override
	public E set(int index, E element)
	{
		rangeCheck(index);

		E e = null;
		e = (E) elementData[index];
		elementData[index] = element;

		return e;
	}

	/**
	 * Вставить указанный элемент в указанную позицию списка, при этом все элементы в этой позиции сдвигаются направо
	 * @param index позиция, в которую необходимо вставить указанный элемент
	 * @param element элемент для вставки
	 * @throws IndexOutOfBoundsException в случае, если заданная позиция выходит за пределы размерности списка
	 */
	@Override
	public void add(int index, E element)
	{
		rangeCheck(index);

		ensureCapacity(size + 1);
		System.arraycopy(elementData, index, elementData, index + 1, size - index);
		elementData[index] = element;
		size++;
	}

	/**
	 * Вставить элементы коллекции в указанную позицию списка, при этом все элементы в этой позиции сдвигаются направо.
	 * Для получения элементов коллекции используется метод {@link Collection#toArray() Collection.toArray()}
	 * @param index позиция, в которую необходимо вставить элементы указанной коллекции
	 * @param c коллекция, которая содержит элементы для вставки
	 * @return true, если список был изменен
	 * @throws IndexOutOfBoundsException в случае, если заданная позиция выходит за пределы размерности списка
	 */
	@Override
	public boolean addAll(int index, Collection<? extends E> c)
	{
		rangeCheck(index);

		if(c == null || c.isEmpty())
			return false;

		Object[] a = c.toArray();
		int numNew = a.length;
		ensureCapacity(size + numNew);

		int numMoved = size - index;
		if(numMoved > 0)
			System.arraycopy(elementData, index, elementData, index + numNew, numMoved);
		System.arraycopy(a, 0, elementData, index, numNew);
		size += numNew;

		return true;
	}

	/**
	 * Расширить внутренний массив так, чтобы он смог разместить как минимум <b>newSize</b> элементов
	 * @param newSize минимальная размерность нового массива
	 */
	protected void ensureCapacity(int newSize)
	{
		if(newSize > capacity)
		{
			if(newSize < H)
				while(capacity < newSize)
					capacity <<= 1;
			else
				while(capacity < newSize)
					capacity = capacity * 3 / 2;

			Object[] elementDataResized = new Object[capacity];
			if(elementData != null)
				System.arraycopy(elementData, 0, elementDataResized, 0, size);
			elementData = elementDataResized;
		}
		else // Инициализация массива
			if(elementData == null)
				elementData = new Object[capacity];
	}

	protected void rangeCheck(int index)
	{
		if(index < 0 || index >= size)
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
	}

	/**
	 * Удалить элемент в указаной позиции. При этом элементы не сдвигаются влево, а в указанную позицию, вместо удаленного, помещается элемент с конца списка
	 * @param index позиция, в которой следует удалить элемент
	 * @return элемент, удаленный из указанной позиции
	 * @throws IndexOutOfBoundsException в случае, если заданная позиция выходит за пределы размерности списка
	 */
	@Override
	public E remove(int index)
	{
		rangeCheck(index);

		E e = null;
		size--;
		e = (E) elementData[index];
		elementData[index] = elementData[size];
		elementData[size] = null;

		trim();

		return e;
	}

	/**
	 * Удалить из списка первое вхождение объекта, может быть <tt>null</tt>, при проверке идиентичности используется оператор <tt>==</tt>
	 * @param o объект, который следует удалить из списка
	 * @return true, если объект находился в списке
	 */
	@Override
	public boolean remove(Object o)
	{
		if(size == 0)
			return false;

		int index = -1;
		for(int i = 0; i < size; i++)
			if(elementData[i] == o)
			{
				index = i;
				break;
			}

		if(index == -1)
			return false;

		size--;
		elementData[index] = elementData[size];
		elementData[size] = null;

		trim();

		return true;
	}

	/**
	 * Возвращает true, если объект содержится в списке, в качестве аргумента может быть <tt>null</tt>, при проверке идиентичности используется оператор <tt>==</tt>
	 * @param o объект, присутствие которого проверяется в списке
	 * @return true, если объект находится в списке
	 */
	@Override
	public boolean contains(Object o)
	{
		if(size == 0)
			return false;

		for(int i = 0; i < size; i++)
			if(elementData[i] == o)
				return true;

		return false;
	}

	/**
	 * Возвращает позицию первого вхождения объекта в списке, в качестве аргумента может быть <tt>null</tt>, при проверке идиентичности используется оператор <tt>==</tt>, если объект не найден, возвращает -1
	 * @param o объект для поиска в списке
	 * @return позиция, в которой находится объект в списке, либо -1, если объект не найден
	 */
	@Override
	public int indexOf(Object o)
	{
		if(size == 0)
			return -1;

		int index = -1;
		for(int i = 0; i < size; i++)
			if(elementData[i] == o)
			{
				index = i;
				break;
			}

		return index;
	}

	/**
	 * Возвращает позицию последнего вхождения объекта в списке, в качестве аргумента может быть <tt>null</tt>, при проверке идиентичности используется оператор <tt>==</tt>, если объект не найден, возвращает -1
	 * @param o объект для поиска в списке
	 * @return последняя позиция, в которой находится объект в списке, либо -1, если объект не найден
	 */
	@Override
	public int lastIndexOf(Object o)
	{
		if(size == 0)
			return -1;

		int index = -1;
		for(int i = 0; i < size; i++)
			if(elementData[i] == o)
				index = i;

		return index;
	}

	protected void trim()
	{

	}

	/**
	 * Получить элемент списка в заданной позиции
	 * @param index позиция списка, элемент из которой необходимо получить
	 * @return возвращает элемент списка в заданной позиции
	 * @throws IndexOutOfBoundsException в случае, если заданная позиция выходит за пределы размерности списка
	 */
	@Override
	public E get(int index)
	{
		rangeCheck(index);

		return (E) elementData[index];
	}

	/**
	 * Получить копию списка
	 * @return список, с параметрами и набором элементов текущего
	 */
	@Override
	public Object clone()
	{
		LazyArrayList<E> clone = new LazyArrayList<E>();
		if(size > 0)
		{
			clone.capacity = capacity;
			clone.elementData = new Object[elementData.length];
			System.arraycopy(elementData, 0, clone.elementData, 0, size);
		}
		return clone;
	}

	/**
	 * Очистить список
	 */
	@Override
	public void clear()
	{
		if(size == 0)
			return;

		for(int i = 0; i < size; i++)
			elementData[i] = null;

		size = 0;
		trim();
	}

	/**
	 * Возвращает количество элементов в списке
	 * @return количество элементов в списке
	 */
	@Override
	public int size()
	{
		return size;
	}

	/**
	 * Возвращает true, если список не содержит элементов
	 * @return true, если список пуст
	 */
	@Override
	public boolean isEmpty()
	{
		return size == 0;
	}

	/**
	 * Возвращает размер внутреннего массива списка
	 * @return размер внутреннего массива
	 */
	public int capacity()
	{
		return capacity;
	}

	/**
	 * Добавить все элементы коллекции в список. Для получения элементов коллекции используется метод {@link Collection#toArray() Collection.toArray()}
	 * @param c коллекция, которая содержит элементы для добавления
	 * @return true, если список был изменен
	 */
	@Override
	public boolean addAll(Collection<? extends E> c)
	{
		if(c == null || c.isEmpty())
			return false;
		Object[] a = c.toArray();
		int numNew = a.length;
		ensureCapacity(size + numNew);
		System.arraycopy(a, 0, elementData, size, numNew);
		size += numNew;
		return true;
	}

	/**
	 * Проверяет, содержатся ли все элементы коллекции в списке
	 * @param c коллекция, которая содержит элементы для проверки нахождения в списке
	 * @return true, если список содержит все элементы коллекции
	 * @see #contains(Object)
	 */
	@Override
	public boolean containsAll(Collection<?> c)
	{
		if(c == null)
			return false;
		if(c.isEmpty())
			return true;
		Iterator<?> e = c.iterator();
		while(e.hasNext())
			if(!contains(e.next()))
				return false;
		return true;
	}

	/**
	 * Удаляет из списка все элементы, которые не содержатся в заданной коллекции, для проверки нахождения элемента в коллекции используется метод коллекции {@link Collection#contains(Object) Collection.contains(Object)}
	 * @param c коллекция, которая содержит элементы, которые необходимо оставить в списке
	 * @return true, если список был изменен
	 */
	@Override
	public boolean retainAll(Collection<?> c)
	{
		if(c == null)
			return false;
		boolean modified = false;
		Iterator<E> e = iterator();
		while(e.hasNext())
			if(!c.contains(e.next()))
			{
				e.remove();
				modified = true;
			}
		return modified;
	}

	/**
	 * Удаляет из списка все элементы, которые содержатся в заданной коллекции, для проверки нахождения элемента в коллекции используется метод коллекции {@link Collection#contains(Object) Collection.contains(Object)}
	 * @param c коллекция, которая содержит элементы для удаления из списка
	 * @return true, если список был изменен
	 */
	@Override
	public boolean removeAll(Collection<?> c)
	{
		if(c == null || c.isEmpty())
			return false;
		boolean modified = false;
		Iterator<?> e = iterator();
		while(e.hasNext())
			if(c.contains(e.next()))
			{
				e.remove();
				modified = true;
			}
		return modified;
	}

	@Override
	public Object[] toArray()
	{
		Object[] r = new Object[size];
		if(size > 0)
			System.arraycopy(elementData, 0, r, 0, size);
		return r;
	}

	@Override
	public <T> T[] toArray(T[] a)
	{
		T[] r = a.length >= size ? a : (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
		if(size > 0)
			System.arraycopy(elementData, 0, r, 0, size);
		if(r.length > size)
			r[size] = null;
		return r;
	}

	@Override
	public Iterator<E> iterator()
	{
		return new LazyItr();
	}

	@Override
	public ListIterator<E> listIterator()
	{
		return new LazyListItr(0);
	}

	@Override
	public ListIterator<E> listIterator(int index)
	{
		return new LazyListItr(index);
	}

	private class LazyItr implements Iterator<E>
	{
		int cursor = 0;
		int lastRet = -1;

		@Override
		public boolean hasNext()
		{
			return cursor < size();
		}

		@Override
		public E next()
		{
			E next = get(cursor);
			lastRet = cursor++;
			return next;
		}

		@Override
		public void remove()
		{
			if(lastRet == -1)
				throw new IllegalStateException();
			LazyArrayList.this.remove(lastRet);
			if(lastRet < cursor)
				cursor--;
			lastRet = -1;
		}
	}

	private class LazyListItr extends LazyItr implements ListIterator<E>
	{
		LazyListItr(int index)
		{
			cursor = index;
		}

		@Override
		public boolean hasPrevious()
		{
			return cursor > 0;
		}

		@Override
		public E previous()
		{
			int i = cursor - 1;
			E previous = get(i);
			lastRet = cursor = i;
			return previous;
		}

		@Override
		public int nextIndex()
		{
			return cursor;
		}

		@Override
		public int previousIndex()
		{
			return cursor - 1;
		}

		@Override
		public void set(E e)
		{
			if(lastRet == -1)
				throw new IllegalStateException();
			LazyArrayList.this.set(lastRet, e);
		}

		@Override
		public void add(E e)
		{
			LazyArrayList.this.add(cursor++, e);
			lastRet = -1;
		}
	}

	@Override
	public String toString()
	{
		if(size == 0)
			return "[]";

		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for(int i = 0; i < size; i++)
		{
			Object e = elementData[i];
			sb.append(e == this ? "this" : e);

			if(i == size - 1)
				sb.append(']');
			else
				sb.append(", ");
		}
		return sb.toString();
	}

	/**
	 * Метод не реализован
	 * @throws UnsupportedOperationException
	 */
	@Override
	public List<E> subList(int fromIndex, int toIndex)
	{
		throw new UnsupportedOperationException();
	}
}
