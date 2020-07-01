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
package org.l2j.gameserver.network;

import io.github.joealisson.mmocore.PacketBuffer;
import io.github.joealisson.mmocore.PacketHandler;
import io.github.joealisson.mmocore.ReadablePacket;
import org.l2j.commons.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Byte.toUnsignedInt;
import static java.lang.Integer.toHexString;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public class ClientPacketHandler implements PacketHandler<GameClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientPacketHandler.class);
    private static final Logger ST_PACKET = LoggerFactory.getLogger("ST_PACKET");

    @Override
    public ReadablePacket<GameClient> handlePacket(PacketBuffer buffer, GameClient client) {
        ST_PACKET.debug("{}",buffer.remaining());
        var opcode = toUnsignedInt(buffer.read());

        if(opcode >= IncomingPackets.PACKET_ARRAY.length) {
            unknownPacket(buffer, opcode, null);
            return null;
        }   

        PacketFactory packetFactory = getPacketFactory(opcode, buffer);

        return makePacketWithFactory(buffer, client, opcode, packetFactory);
    }

    private ReadablePacket<GameClient> makePacketWithFactory(PacketBuffer buffer, GameClient client, int opcode, PacketFactory packetFactory) {
        ReadablePacket<GameClient> packet;

        if (isNull(packetFactory) || isNull((packet = packetFactory.newIncomingPacket()))) {
            unknownPacket(buffer, opcode, packetFactory);
            return null;
        }

        if(packet instanceof DiscardPacket) {
            return null;
        }

        final ConnectionState connectionState = client.getConnectionState();
        if (!packetFactory.canHandleState(client.getConnectionState())) {
            LOGGER.warn("Client {} sent packet {} at invalid state {} Required States: {} - [{}]: {}", client, packetFactory, connectionState, packetFactory.getConnectionStates(), toHexString(opcode), CommonUtil.printData(buffer.expose()));
            return null;
        }
        if(ConnectionState.JOINING_GAME_AND_IN_GAME.contains(connectionState) && isNull(client.getPlayer())) {
            LOGGER.warn("Client {} sent IN_GAME packet {} without a player", client, packetFactory);
            return null;
        }

        return packet;
    }

    private void unknownPacket(PacketBuffer buffer, int opcode, PacketFactory packetFactory) {
        LOGGER.debug("Unknown Packet ({}) : {} - {}", packetFactory, toHexString(opcode), CommonUtil.printData(buffer.expose()));
    }

    private PacketFactory getPacketFactory(int opcode, PacketBuffer buffer) {
        IncomingPackets packetFactory = IncomingPackets.PACKET_ARRAY[opcode];
        if(nonNull(packetFactory) && packetFactory.hasExtension()) {
            return packetFactory.handleExtension(buffer);
        }
        return packetFactory;
    }
}
