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
package org.l2j.gameserver.network.clientpackets;

import io.github.joealisson.mmocore.ReadablePacket;
import org.l2j.gameserver.GameServer;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Packets received by the game server from clients
 *
 * @author KenM
 */
public abstract class ClientPacket extends ReadablePacket<GameClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientPacket.class);

    @Override
    protected boolean read() {
        try {
            readImpl();
            return true;
        } catch (InvalidDataPacketException e) {
            LOGGER.warn("[{}] Invalid data packet {} from client {}", GameServer.fullVersion, this, client);
        } catch (Exception e) {
            LOGGER.error("[{}] Error while reading packet {} from client {}", GameServer.fullVersion, this, client);
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public void run() {
        try {
            runImpl();
        } catch (Exception e) {
            LOGGER.error("[{}] Error while running packet {} from client {}", GameServer.fullVersion, this, client);
            LOGGER.error(e.getMessage(), e);
        }
    }

    protected abstract void runImpl() throws Exception;

    protected abstract void readImpl() throws Exception;
}
