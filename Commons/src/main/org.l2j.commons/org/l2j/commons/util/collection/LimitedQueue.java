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
package org.l2j.commons.util.collection;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Spliterator;

/**
 *
 * Limited Evicting Queue. When max size is reached the first element is removed.
 *
 * @author JoeAlisson
 */
public class LimitedQueue<E> extends AbstractCollection<E> {

    private final LinkedList<E> queue = new LinkedList<>();
    private final int maxSize;

    public LimitedQueue(int maxElements) {
        this.maxSize = maxElements;
    }

    public synchronized boolean add(E e) {
        queue.addLast(e);
        if(queue.size() > maxSize) {
            queue.removeFirst();
        }
        return true;
    }

    @Override
    public Iterator<E> iterator() {
        return queue.iterator();
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public Spliterator<E> spliterator() {
        return queue.spliterator();
    }
}
