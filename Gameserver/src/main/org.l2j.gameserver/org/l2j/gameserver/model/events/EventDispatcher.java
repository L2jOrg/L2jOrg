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
package org.l2j.gameserver.model.events;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import org.l2j.gameserver.model.events.returns.AbstractEventReturn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;

/**
 * @author UnAfraid
 */
public final class EventDispatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventDispatcher.class);

    private EventDispatcher() {
    }

    /**
     * @param <T>
     * @param event
     * @return
     */
    public <T extends AbstractEventReturn> T notifyEvent(IBaseEvent event) {
        return notifyEvent(event, null, null);
    }

    /**
     * @param <T>
     * @param event
     * @param callbackClass
     * @return
     */
    public <T extends AbstractEventReturn> T notifyEvent(IBaseEvent event, Class<T> callbackClass) {
        return notifyEvent(event, null, callbackClass);
    }

    /**
     * @param <T>
     * @param event
     * @param container
     * @return
     */
    public <T extends AbstractEventReturn> T notifyEvent(IBaseEvent event, ListenersContainer container) {
        return notifyEvent(event, container, null);
    }

    /**
     * @param <T>
     * @param event
     * @param container
     * @param callbackClass
     * @return
     */
    public <T extends AbstractEventReturn> T notifyEvent(IBaseEvent event, ListenersContainer container, Class<T> callbackClass) {
        try {
            return Listeners.Global().hasListener(event.getType()) || ((container != null) && container.hasListener(event.getType())) ? notifyEventImpl(event, container, callbackClass) : null;
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Couldn't notify event " + event.getClass().getSimpleName(), e);
        }
        return null;
    }

    /**
     * Executing current listener notification asynchronously
     *
     * @param event
     * @param containers
     */
    public void notifyEventAsync(IBaseEvent event, ListenersContainer... containers) {
        if (event == null) {
            throw new NullPointerException("Event cannot be null!");
        }

        boolean hasListeners = Listeners.Global().hasListener(event.getType());
        if (!hasListeners) {
            for (ListenersContainer container : containers) {
                if (container.hasListener(event.getType())) {
                    hasListeners = true;
                    break;
                }
            }
        }

        if (hasListeners) {
            ThreadPool.execute(() -> notifyEventToMultipleContainers(event, containers, null));
        }
    }

    /**
     * Scheduling current listener notification asynchronously after specified delay.
     *
     * @param event
     * @param container
     * @param delay
     */
    public void notifyEventAsyncDelayed(IBaseEvent event, ListenersContainer container, long delay) {
        if (Listeners.Global().hasListener(event.getType()) || container.hasListener(event.getType())) {
            ThreadPool.schedule(() -> notifyEvent(event, container, null), delay);
        }
    }

    /**
     * @param <T>
     * @param event
     * @param containers
     * @param callbackClass
     * @return
     */
    private <T extends AbstractEventReturn> T notifyEventToMultipleContainers(IBaseEvent event, ListenersContainer[] containers, Class<T> callbackClass) {
        try {
            if (event == null) {
                throw new NullPointerException("Event cannot be null!");
            }

            T callback = null;
            if (containers != null) {
                // Local listeners container first.
                for (ListenersContainer container : containers) {
                    if ((callback == null) || !callback.abort()) {
                        callback = notifyToListeners(container.getListeners(event.getType()), event, callbackClass, callback);
                    }
                }
            }

            // Global listener container.
            if ((callback == null) || !callback.abort()) {
                callback = notifyToListeners(Listeners.Global().getListeners(event.getType()), event, callbackClass, callback);
            }

            return callback;
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Couldn't notify event " + event.getClass().getSimpleName(), e);
        }
        return null;
    }

    /**
     * @param <T>
     * @param event
     * @param container
     * @param callbackClass
     * @return {@link AbstractEventReturn} object that may keep data from the first listener, or last that breaks notification.
     */
    private <T extends AbstractEventReturn> T notifyEventImpl(IBaseEvent event, ListenersContainer container, Class<T> callbackClass) {
        if (event == null) {
            throw new NullPointerException("Event cannot be null!");
        }

        T callback = null;
        // Local listener container first.
        if (container != null) {
            callback = notifyToListeners(container.getListeners(event.getType()), event, callbackClass, callback);
        }

        // Global listener container.
        if ((callback == null) || !callback.abort()) {
            callback = notifyToListeners(Listeners.Global().getListeners(event.getType()), event, callbackClass, callback);
        }

        return callback;
    }

    /**
     * @param <T>
     * @param listeners
     * @param event
     * @param returnBackClass
     * @param callback
     * @return
     */
    private <T extends AbstractEventReturn> T notifyToListeners(Queue<AbstractEventListener> listeners, IBaseEvent event, Class<T> returnBackClass, T callback) {
        for (AbstractEventListener listener : listeners) {
            try {
                final T rb = listener.executeEvent(event, returnBackClass);
                if (rb == null) {
                    continue;
                } else if ((callback == null) || rb.override()) // Let's check if this listener wants to override previous return object or we simply don't have one
                {
                    callback = rb;
                } else if (rb.abort()) // This listener wants to abort the notification to others.
                {
                    break;
                }
            } catch (Exception e) {
                LOGGER.warn("Exception during notification of event: {} listener {} : {}", event.getClass().getSimpleName(),listener.getClass().getSimpleName(), e.getCause());

            }
        }

        return callback;
    }

    public static EventDispatcher getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final EventDispatcher INSTANCE = new EventDispatcher();
    }
}
