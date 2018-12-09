package org.l2j.commons.util;

public class Pair<T, S> {
    private T one;
    private S other;

    public Pair(T one, S other) {
        this.one = one;
        this.other = other;
    }

    public T getKey() {
        return one;
    }

    public S getRight() {
        return other;
    }

    public S getValue() {
        return other;
    }
}
