/*
 * Copyright © 2019-2021 L2JOrg
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
public class VoicedCommandHandler implements IHandler<IVoicedCommandHandler, String> {
    private final Map<String, IVoicedCommandHandler> _datatable;

    private VoicedCommandHandler() {
        _datatable = new HashMap<>();
    }

    @Override
    public void registerHandler(IVoicedCommandHandler handler) {
        for (String id : handler.getVoicedCommandList()) {
            _datatable.put(id, handler);
        }
    }

    @Override
    public synchronized void removeHandler(IVoicedCommandHandler handler) {
        for (String id : handler.getVoicedCommandList()) {
            _datatable.remove(id);
        }
    }

    @Override
    public IVoicedCommandHandler getHandler(String voicedCommand) {
        return _datatable.get(voicedCommand.contains(" ") ? voicedCommand.substring(0, voicedCommand.indexOf(" ")) : voicedCommand);
    }

    @Override
    public int size() {
        return _datatable.size();
    }

    public static VoicedCommandHandler getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final VoicedCommandHandler INSTANCE = new VoicedCommandHandler();
    }
}
