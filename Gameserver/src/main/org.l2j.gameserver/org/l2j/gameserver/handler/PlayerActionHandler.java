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
 * @author UnAfraid
 */
public class PlayerActionHandler implements IHandler<IPlayerActionHandler, String> {
    private final Map<String, IPlayerActionHandler> _actions = new HashMap<>();

    private PlayerActionHandler() {
    }

    @Override
    public void registerHandler(IPlayerActionHandler handler) {
        _actions.put(handler.getClass().getSimpleName(), handler);
    }

    @Override
    public synchronized void removeHandler(IPlayerActionHandler handler) {
        _actions.remove(handler.getClass().getSimpleName());
    }

    @Override
    public IPlayerActionHandler getHandler(String name) {
        return _actions.get(name);
    }

    @Override
    public int size() {
        return _actions.size();
    }

    public static PlayerActionHandler getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final PlayerActionHandler INSTANCE = new PlayerActionHandler();
    }
}