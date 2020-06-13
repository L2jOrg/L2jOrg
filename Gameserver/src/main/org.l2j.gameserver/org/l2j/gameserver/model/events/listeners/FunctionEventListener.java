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

import java.util.function.Function;


/**
 * Function event listener provides callback operation with return object possibility.
 *
 * @author UnAfraid
 */
public class FunctionEventListener extends AbstractEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionEventListener.class);
    private final Function<IBaseEvent, ? extends AbstractEventReturn> _callback;

    @SuppressWarnings("unchecked")
    public FunctionEventListener(ListenersContainer container, EventType type, Function<? extends IBaseEvent, ? extends AbstractEventReturn> callback, Object owner) {
        super(container, type, owner);
        _callback = (Function<IBaseEvent, ? extends AbstractEventReturn>) callback;
    }

    @Override
    public <R extends AbstractEventReturn> R executeEvent(IBaseEvent event, Class<R> returnBackClass) {
        try {
            return returnBackClass.cast(_callback.apply(event));
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Error while invoking " + event + " on " + getOwner(), e);
        }
        return null;
    }
}
