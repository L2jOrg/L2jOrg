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

import org.l2j.gameserver.model.skills.targets.TargetType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author UnAfraid
 */
public class TargetHandler implements IHandler<ITargetTypeHandler, Enum<TargetType>> {
    private final Map<Enum<TargetType>, ITargetTypeHandler> _datatable;

    private TargetHandler() {
        _datatable = new HashMap<>();
    }

    @Override
    public void registerHandler(ITargetTypeHandler handler) {
        _datatable.put(handler.getTargetType(), handler);
    }

    @Override
    public synchronized void removeHandler(ITargetTypeHandler handler) {
        _datatable.remove(handler.getTargetType());
    }

    @Override
    public ITargetTypeHandler getHandler(Enum<TargetType> targetType) {
        return _datatable.get(targetType);
    }

    @Override
    public int size() {
        return _datatable.size();
    }

    public static TargetHandler getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {

        protected static final TargetHandler INSTANCE = new TargetHandler();
    }
}
