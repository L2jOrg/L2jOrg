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
