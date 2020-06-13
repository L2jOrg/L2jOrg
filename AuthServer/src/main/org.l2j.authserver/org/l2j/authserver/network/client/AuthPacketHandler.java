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
package org.l2j.authserver.network.client;

import io.github.joealisson.mmocore.PacketBuffer;
import io.github.joealisson.mmocore.PacketHandler;
import io.github.joealisson.mmocore.ReadablePacket;
import org.l2j.authserver.network.client.packet.client2auth.AuthGameGuard;
import org.l2j.authserver.network.client.packet.client2auth.RequestAuthLogin;
import org.l2j.authserver.network.client.packet.client2auth.RequestServerList;
import org.l2j.authserver.network.client.packet.client2auth.RequestServerLogin;
import org.l2j.commons.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Byte.toUnsignedInt;

/**
 * Handler for packets received by Login Server
 *
 * @author KenM
 */
public final class AuthPacketHandler implements PacketHandler<AuthClient> {

    private static final Logger logger = LoggerFactory.getLogger(AuthPacketHandler.class);

    @Override
    public ReadablePacket<AuthClient> handlePacket(PacketBuffer buffer, AuthClient client) {
        var opcode = toUnsignedInt(buffer.read());

        ReadablePacket<AuthClient> packet = null;
        var state = client.getState();

        switch (state) {
            case CONNECTED:
                if (opcode == 0x07) {
                    packet = new AuthGameGuard();
                } else {
                    debugOpcode(opcode, buffer, state);
                }
                break;
            case AUTHED_GG:
                if (opcode == 0x00) {
                    packet = new RequestAuthLogin();
                } else {
                    debugOpcode(opcode, buffer, state);
                }
                break;
            case AUTHED_LOGIN:
                if (opcode == 0x05) {
                    packet = new RequestServerList();
                } else if (opcode == 0x02) {
                    packet = new RequestServerLogin();
                } else {
                    debugOpcode(opcode, buffer, state);
                }
                break;
        }
        return packet;
    }

    private void debugOpcode(int opcode, PacketBuffer data, AuthClientState state) {
        logger.warn("Unknown Opcode: {} for state {}\n {}", Integer.toHexString(opcode), state, CommonUtil.printData(data.expose()));
    }
}
