package org.l2j.gameserver.model.events.listeners;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.returns.AbstractEventReturn;

import java.util.Observer;

/**
 * @author UnAfraid
 */
public abstract class AbstractEventListener implements Comparable<AbstractEventListener> {
    private final ListenersContainer _container;
    private final EventType _type;
    private final Object _owner;
    private int _priority = 0;

    public AbstractEventListener(ListenersContainer container, EventType type, Object owner) {
        _container = container;
        _type = type;
        _owner = owner;
    }

    /**
     * @return the container on which this listener is being registered (Used to unregister when unloading scripts)
     */
    public ListenersContainer getContainer() {
        return _container;
    }

    /**
     * @return the type of event which listener is listening for.
     */
    public EventType getType() {
        return _type;
    }

    /**
     * @return the owner of the listener, the object that registered this listener.
     */
    public Object getOwner() {
        return _owner;
    }

    /**
     * @return priority of execution (Higher the sooner)
     */
    public int getPriority() {
        return _priority;
    }

    /**
     * Sets priority of execution.
     *
     * @param priority
     */
    public void setPriority(int priority) {
        _priority = priority;
    }

    /**
     * Method invoked by EventDispatcher that will use the callback.
     *
     * @param <R>
     * @param event
     * @param returnBackClass
     * @return
     */
    public abstract <R extends AbstractEventReturn> R executeEvent(IBaseEvent event, Class<R> returnBackClass);

    /**
     * Unregisters detaches and unregisters current listener.
     */
    public void unregisterMe() {
        _container.removeListener(this);
    }

    @Override
    public int compareTo(AbstractEventListener o) {
        return Integer.compare(o.getPriority(), getPriority());
    }
}
