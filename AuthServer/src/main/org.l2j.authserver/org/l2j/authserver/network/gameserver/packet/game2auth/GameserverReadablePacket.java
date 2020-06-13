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
package org.l2j.authserver.network.gameserver.packet.game2auth;

import io.github.joealisson.mmocore.ReadablePacket;
import org.l2j.authserver.network.gameserver.ServerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GameserverReadablePacket extends ReadablePacket<ServerClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameserverReadablePacket.class);

    @Override
    protected boolean read() {
        try {
            readImpl();
        } catch (Exception e) {
            LOGGER.error("Reading {} : {} ", getClass().getSimpleName(), e);
            LOGGER.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public void run() {
        try {
            runImpl();
        } catch (Exception e) {
            LOGGER.error("Running {} : {} ", getClass().getSimpleName(), e);
            LOGGER.error(e.getMessage(), e);
        }
    }

    protected abstract void readImpl() throws  Exception;
    protected abstract void runImpl() throws Exception;
}
