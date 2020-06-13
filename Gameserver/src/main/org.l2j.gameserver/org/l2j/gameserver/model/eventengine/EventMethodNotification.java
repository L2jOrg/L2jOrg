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
package org.l2j.gameserver.model.eventengine;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * @author UnAfraid
 */
public class EventMethodNotification {
    private final AbstractEventManager<?> _manager;
    private final Method _method;
    private final Object[] _args;

    /**
     * @param manager
     * @param methodName
     * @param args
     * @throws NoSuchMethodException
     */
    public EventMethodNotification(AbstractEventManager<?> manager, String methodName, List<Object> args) throws NoSuchMethodException {
        _manager = manager;
        _method = manager.getClass().getDeclaredMethod(methodName, args.stream().map(Object::getClass).toArray(Class[]::new));
        _args = args.toArray();
    }

    public AbstractEventManager<?> getManager() {
        return _manager;
    }

    public Method getMethod() {
        return _method;
    }

    public void execute() throws Exception {
        if (Modifier.isStatic(_method.getModifiers())) {
            invoke(null);
        } else {
            // Attempt to find getInstance() method
            for (Method method : _manager.getClass().getMethods()) {
                if (Modifier.isStatic(method.getModifiers()) && (_manager.getClass().isAssignableFrom(method.getReturnType())) && (method.getParameterCount() == 0)) {
                    final Object instance = method.invoke(null);
                    invoke(instance);
                }
            }
        }
    }

    private void invoke(Object instance) throws Exception {
        final boolean wasAccessible = _method.canAccess(instance);
        if (!wasAccessible) {
            _method.setAccessible(true);
        }
        _method.invoke(instance, _args);
        _method.setAccessible(wasAccessible);
    }
}
