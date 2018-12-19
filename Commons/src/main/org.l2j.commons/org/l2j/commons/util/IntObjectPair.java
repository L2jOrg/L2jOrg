package org.l2j.commons.util;

public class IntObjectPair<T> {
    private int first;
    private T last;

    public IntObjectPair(int first, T last) {
        this.first = first;
        this.last = last;
    }


    public T getValue() {
        return last;
    }

    public int getKey() {
        return first;
    }
}
