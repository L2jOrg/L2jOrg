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

import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * Limited Evicting Queue. When max size is reached the first element is removed.
 *
 * @author JoeAlisson
 */
public class LimitedQueue<E> extends LinkedList<E> {

    private final int maxSize;

    public LimitedQueue(int maxElements) {
        this.maxSize = maxElements;
    }

    @Override
    public void addFirst(E e) {
        if(size() + 1 > maxSize) {
            removeFirst();
        }
        super.addFirst(e);
    }

    @Override
    public void addLast(E e) {
        super.addLast(e);
        if(size() > maxSize) {
            removeFirst();
        }
    }

    @Override
    public boolean add(E e) {
        addLast(e);
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if(super.addAll(index, c)){
            while (size() > maxSize){
                removeFirst();
            }
            return true;
        }
        return false;
    }

    @Override
    public E set(int index, E element) {
        E e = super.set(index, element);
        if (size()  > maxSize){
            removeFirst();
        }
        return e;
    }
}
