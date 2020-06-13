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
package org.l2j.authserver.network.gameserver;

import io.github.joealisson.mmocore.PacketBuffer;
import io.github.joealisson.mmocore.PacketExecutor;
import io.github.joealisson.mmocore.PacketHandler;
import io.github.joealisson.mmocore.ReadablePacket;
import org.l2j.authserver.network.gameserver.packet.game2auth.*;
import org.l2j.commons.threading.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Byte.toUnsignedInt;
import static org.l2j.authserver.network.gameserver.packet.auth2game.LoginGameServerFail.NOT_AUTHED;

public final class GameServerPacketHandler implements PacketHandler<ServerClient>, PacketExecutor<ServerClient> {

    private static final Logger logger = LoggerFactory.getLogger(GameServerPacketHandler.class);

    @Override
    public ReadablePacket<ServerClient> handlePacket(PacketBuffer buffer, ServerClient client) {
        var opcode = toUnsignedInt(buffer.read());
        switch (client.getState()) {
            case CONNECTED:
                return handlePacketInConnected(client, opcode);
            case AUTHED:
                return handleAuthedPacket(client, opcode);
            default:
                handleUnkownOpcode(client, opcode);
        }
        return null;
    }

    private ReadablePacket<ServerClient> handleAuthedPacket(ServerClient client, int opcode) {
        ReadablePacket<ServerClient> packet = null;
        switch (opcode) {
            case 0x03:
                packet = new PlayerInGame();
                break;
            case 0x04:
                packet = new PlayerLogout();
                break;
            case 0x11:
                packet = new ChangeAccessLevel();
                break;
            case 0x02:
                packet = new PlayerAuthRequest();
                break;
            case 0x06:
                packet = new ServerStatus();
                break;
            case 0x05:
                packet = new AccountInfo();
                break;
            default:
                handleUnkownOpcode(client, opcode);
                break;
        }
        return packet;
    }

    private ReadablePacket<ServerClient> handlePacketInConnected(ServerClient client, int opcode) {
        ReadablePacket<ServerClient> packet = null;
        switch (opcode) {
            case 0x00:
                packet = new AuthRequest();
                break;
            default:
                handleUnkownOpcode(client, opcode);
                break;
        }
        return packet;
    }

    private void handleUnkownOpcode(ServerClient client, int opcode) {
        logger.warn("Unknown Opcode ({}) on state {} from ServerInfo, closing socket.", Integer.toHexString(opcode).toUpperCase(), client.getState());
        client.close(NOT_AUTHED);
    }

    @Override
    public void execute(ReadablePacket<ServerClient> packet) {
        ThreadPool.execute(packet);
    }
}
