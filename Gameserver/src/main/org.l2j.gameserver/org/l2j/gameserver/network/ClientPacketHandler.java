package org.l2j.gameserver.network;

import io.github.joealisson.mmocore.PacketHandler;
import io.github.joealisson.mmocore.ReadablePacket;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.network.l2.GamePacketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

import static java.lang.Byte.toUnsignedInt;
import static java.lang.Integer.toHexString;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class ClientPacketHandler implements PacketHandler<L2GameClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GamePacketHandler.class);

    @Override
    public ReadablePacket<L2GameClient> handlePacket(ByteBuffer buffer, L2GameClient client) {
        var opcode = toUnsignedInt(buffer.get());

        if(opcode >= IncomingPackets.PACKET_ARRAY.length) {
            unknownPacket(buffer, opcode);
            return null;
        }

        PacketFactory packetFactory = getPacketFactory(opcode, buffer);

        ReadablePacket<L2GameClient> packet;

        if (isNull(packetFactory) || isNull((packet = packetFactory.newIncomingPacket()))) {
            unknownPacket(buffer, opcode);
            return null;
        }

        final ConnectionState connectionState = client.getConnectionState();
        if (!packetFactory.canHandleState(client.getConnectionState())) {
            LOGGER.warn("Client {} sent packet {} at invalid state {} Required States: {}", client, toHexString(opcode),  connectionState, packetFactory.getConnectionStates());
            return null;
        }
        return packet;
    }

    private void unknownPacket(ByteBuffer buffer, int opcode) {
        LOGGER.warn("Unknown packet: {} - {}", toHexString(opcode), Util.printData(buffer.array()));
    }

    private PacketFactory getPacketFactory(int opcode, ByteBuffer buffer) {
        IncomingPackets packetFactory = IncomingPackets.PACKET_ARRAY[opcode];
        if(nonNull(packetFactory) && packetFactory.hasExtension()) {
            return packetFactory.handleExtension(buffer);
        }
        return packetFactory;
    }
}
