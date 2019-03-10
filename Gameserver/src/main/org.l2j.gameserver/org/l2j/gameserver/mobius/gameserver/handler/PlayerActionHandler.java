/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author UnAfraid
 */
public class PlayerActionHandler implements IHandler<IPlayerActionHandler, String> {
    private final Map<String, IPlayerActionHandler> _actions = new HashMap<>();

    protected PlayerActionHandler() {
    }

    public static PlayerActionHandler getInstance() {
        return SingletonHolder._instance;
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

    private static class SingletonHolder {
        protected static final PlayerActionHandler _instance = new PlayerActionHandler();
    }
}