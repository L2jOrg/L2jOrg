package org.l2j.commons.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;

/**
 * @author UnAfraid
 * @param <E>
 */
public final class EmptyQueue<E> implements Queue<E>
{
    private static final Queue<Object> EMPTY_QUEUE = new EmptyQueue<>();

    @SuppressWarnings("unchecked")
    public static <E> Queue<E> emptyQueue()
    {
        return (Queue<E>) EMPTY_QUEUE;
    }

    @Override
    public int size()
    {
        return 0;
    }

    @Override
    public boolean isEmpty()
    {
        return true;
    }

    @Override
    public boolean contains(Object o)
    {
        return false;
    }

    @Override
    public Iterator<E> iterator()
    {
        return Collections.<E> emptyIterator();
    }

    @Override
    public Object[] toArray()
    {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> c)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(E e)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean offer(E e)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public E remove()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public E poll()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public E element()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public E peek()
    {
        throw new UnsupportedOperationException();
    }
}
