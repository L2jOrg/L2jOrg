/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.model.events.listeners;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.returns.AbstractEventReturn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;


/**
 * Annotation event listener provides dynamically attached callback to any method operation with or without any return object.
 *
 * @author UnAfraid
 */
public class AnnotationEventListener extends AbstractEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationEventListener.class);
    private final Method _callback;

    public AnnotationEventListener(ListenersContainer container, EventType type, Method callback, Object owner, int priority) {
        super(container, type, owner);
        _callback = callback;
        setPriority(priority);
    }

    @Override
    public <R extends AbstractEventReturn> R executeEvent(IBaseEvent event, Class<R> returnBackClass) {
        try {
            final Object result = _callback.invoke(getOwner(), event);
            if (_callback.getReturnType() == returnBackClass) {
                return returnBackClass.cast(result);
            }
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Error while invoking " + _callback.getName() + " on " + getOwner(), e);
        }
        return null;
    }
}
