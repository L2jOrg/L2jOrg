package org.l2j.gameserver.mobius.gameserver.model.interfaces;

/**
 * @param <T>
 * @author UnAfraid
 */
public interface IParameterized<T> {
    T getParameters();

    void setParameters(T set);
}
