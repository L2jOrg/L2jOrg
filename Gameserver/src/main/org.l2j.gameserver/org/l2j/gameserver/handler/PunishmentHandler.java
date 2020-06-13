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

import org.l2j.gameserver.model.punishment.PunishmentType;

import java.util.HashMap;
import java.util.Map;

/**
 * This class manages handlers of punishments.
 *
 * @author UnAfraid
 */
public class PunishmentHandler implements IHandler<IPunishmentHandler, PunishmentType> {
    private final Map<PunishmentType, IPunishmentHandler> _handlers = new HashMap<>();

    private PunishmentHandler() {
    }

    @Override
    public void registerHandler(IPunishmentHandler handler) {
        _handlers.put(handler.getType(), handler);
    }

    @Override
    public synchronized void removeHandler(IPunishmentHandler handler) {
        _handlers.remove(handler.getType());
    }

    @Override
    public IPunishmentHandler getHandler(PunishmentType val) {
        return _handlers.get(val);
    }

    @Override
    public int size() {
        return _handlers.size();
    }

    public static PunishmentHandler getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final PunishmentHandler INSTANCE = new PunishmentHandler();
    }
}
