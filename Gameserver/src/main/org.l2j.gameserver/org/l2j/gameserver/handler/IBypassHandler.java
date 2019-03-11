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
package org.l2j.gameserver.handler;

import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

import java.util.logging.Logger;

/**
 * @author nBd
 */
public interface IBypassHandler {
    Logger LOGGER = Logger.getLogger(IBypassHandler.class.getName());

    /**
     * This is the worker method that is called when someone uses an bypass command.
     *
     * @param command
     * @param activeChar
     * @param bypassOrigin
     * @return success
     */
    boolean useBypass(String command, L2PcInstance activeChar, L2Character bypassOrigin);

    /**
     * This method is called at initialization to register all bypasses automatically.
     *
     * @return all known bypasses
     */
    String[] getBypassList();
}