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
public class UserCommandHandler implements IHandler<IUserCommandHandler, Integer> {
    private final Map<Integer, IUserCommandHandler> _datatable;

    private UserCommandHandler() {
        _datatable = new HashMap<>();
    }

    @Override
    public void registerHandler(IUserCommandHandler handler) {
        for (int id : handler.getUserCommandList()) {
            _datatable.put(id, handler);
        }
    }

    @Override
    public synchronized void removeHandler(IUserCommandHandler handler) {
        for (int id : handler.getUserCommandList()) {
            _datatable.remove(id);
        }
    }

    @Override
    public IUserCommandHandler getHandler(Integer userCommand) {
        return _datatable.get(userCommand);
    }

    @Override
    public int size() {
        return _datatable.size();
    }

    public static UserCommandHandler getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final UserCommandHandler INSTANCE = new UserCommandHandler();
    }
}
