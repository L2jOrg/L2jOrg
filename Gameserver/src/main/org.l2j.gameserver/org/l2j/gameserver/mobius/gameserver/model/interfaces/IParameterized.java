package org.l2j.gameserver.mobius.gameserver.model.interfaces;

/**
 * @author UnAfraid
 * @param <T>
 */
public interface IParameterized<T>
{
    T getParameters();

    void setParameters(T set);
}
