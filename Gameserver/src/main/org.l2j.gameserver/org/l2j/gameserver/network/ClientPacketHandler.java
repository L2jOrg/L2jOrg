package org.l2j.gameserver.network;

import io.github.joealisson.mmocore.PacketHandler;
import io.github.joealisson.mmocore.ReadablePacket;
import org.l2j.commons.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

import static java.lang.Byte.toUnsignedInt;
import static java.lang.Integer.toHexString;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class ClientPacketHandler implements PacketHandler<L2GameClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientPacketHandler.class);

    @Override
    public ReadablePacket<L2GameClient> handlePacket(ByteBuffer buffer, L2GameClient client) {
        var opcode = toUnsignedInt(buffer.get());

        if(opcode >= IncomingPackets.PACKET_ARRAY.length) {
            unknownPacket(buffer, opcode, null);
            return null;
        }

        PacketFactory packetFactory = getPacketFactory(opcode, buffer);

        return MakePacketWithFactory(buffer, client, opcode, packetFactory);
    }

    private ReadablePacket<L2GameClient> MakePacketWithFactory(ByteBuffer buffer, L2GameClient client, int opcode, PacketFactory packetFactory) {
        ReadablePacket<L2GameClient> packet;

        if (isNull(packetFactory) || isNull((packet = packetFactory.newIncomingPacket()))) {
            unknownPacket(buffer, opcode, packetFactory);
            return null;
        }

        if(packet instanceof DiscardPacket) {
            return null;
        }

        final ConnectionState connectionState = client.getConnectionState();
        if (!packetFactory.canHandleState(client.getConnectionState())) {
            LOGGER.warn("Client {} sent packet at invalid state {} Required States: {} - [{}]: {}",
                    client,  connectionState, packetFactory.getConnectionStates(), toHexString(opcode), Util.printData(buffer.array(), buffer.limit()));
            return null;
        }
        return packet;
    }

    private void unknownPacket(ByteBuffer buffer, int opcode, PacketFactory packetFactory) {
        LOGGER.warn("Unknown ({}) packet: {} - {}", packetFactory, toHexString(opcode), Util.printData(buffer.array(), buffer.limit()));
    }

    private PacketFactory getPacketFactory(int opcode, ByteBuffer buffer) {
        IncomingPackets packetFactory = IncomingPackets.PACKET_ARRAY[opcode];
        if(nonNull(packetFactory) && packetFactory.hasExtension()) {
            return packetFactory.handleExtension(buffer);
        }
        return packetFactory;
    }
}
