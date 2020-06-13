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
package org.l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author nBd, UnAfraid
 */
public class BypassHandler implements IHandler<IBypassHandler, String> {
    private final Map<String, IBypassHandler> _datatable;

    private BypassHandler() {
        _datatable = new HashMap<>();
    }

    @Override
    public void registerHandler(IBypassHandler handler) {
        for (String element : handler.getBypassList()) {
            _datatable.put(element.toLowerCase(), handler);
        }
    }

    @Override
    public synchronized void removeHandler(IBypassHandler handler) {
        for (String element : handler.getBypassList()) {
            _datatable.remove(element.toLowerCase());
        }
    }

    @Override
    public IBypassHandler getHandler(String command) {
        if (command.contains(" ")) {
            command = command.substring(0, command.indexOf(" "));
        }
        return _datatable.get(command.toLowerCase());
    }

    @Override
    public int size() {
        return _datatable.size();
    }

    public static BypassHandler getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final BypassHandler INSTANCE = new BypassHandler();
    }
}