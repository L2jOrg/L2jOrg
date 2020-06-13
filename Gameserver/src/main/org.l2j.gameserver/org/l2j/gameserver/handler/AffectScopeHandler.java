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

import org.l2j.gameserver.model.skills.targets.AffectScope;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Nik
 */
public class AffectScopeHandler implements IHandler<IAffectScopeHandler, Enum<AffectScope>> {
    private final Map<Enum<AffectScope>, IAffectScopeHandler> _datatable;

    private AffectScopeHandler() {
        _datatable = new HashMap<>();
    }

    @Override
    public void registerHandler(IAffectScopeHandler handler) {
        _datatable.put(handler.getAffectScopeType(), handler);
    }

    @Override
    public synchronized void removeHandler(IAffectScopeHandler handler) {
        _datatable.remove(handler.getAffectScopeType());
    }

    @Override
    public IAffectScopeHandler getHandler(Enum<AffectScope> affectScope) {
        return _datatable.get(affectScope);
    }

    @Override
    public int size() {
        return _datatable.size();
    }

    public static AffectScopeHandler getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        protected static final AffectScopeHandler INSTANCE = new AffectScopeHandler();
    }
}
