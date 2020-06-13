/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
