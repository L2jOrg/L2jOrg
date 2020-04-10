package org.l2j.gameserver.handler;

/**
 * @param <K>
 * @param <V>
 * @author UnAfraid
 */
public interface IHandler<K, V> {
    void registerHandler(K handler);

    void removeHandler(K handler);

    K getHandler(V val);

    int size();
}
