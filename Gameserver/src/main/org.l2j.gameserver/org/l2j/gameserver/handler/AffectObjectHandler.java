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

import org.l2j.gameserver.model.skills.targets.AffectObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Nik
 */
public class AffectObjectHandler implements IHandler<IAffectObjectHandler, Enum<AffectObject>> {
    private final Map<Enum<AffectObject>, IAffectObjectHandler> _datatable;

    private AffectObjectHandler() {
        _datatable = new HashMap<>();
    }

    @Override
    public void registerHandler(IAffectObjectHandler handler) {
        _datatable.put(handler.getAffectObjectType(), handler);
    }

    @Override
    public synchronized void removeHandler(IAffectObjectHandler handler) {
        _datatable.remove(handler.getAffectObjectType());
    }

    @Override
    public IAffectObjectHandler getHandler(Enum<AffectObject> targetType) {
        return _datatable.get(targetType);
    }

    @Override
    public int size() {
        return _datatable.size();
    }

    public static AffectObjectHandler getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final AffectObjectHandler INSTANCE = new AffectObjectHandler();
    }
}
