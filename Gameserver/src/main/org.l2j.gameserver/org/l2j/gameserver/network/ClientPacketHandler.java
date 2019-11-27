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

public class ClientPacketHandler implements PacketHandler<GameClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientPacketHandler.class);

    @Override
    public ReadablePacket<GameClient> handlePacket(PacketBuffer buffer, GameClient client) {
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
        return packet;
    }

    private void unknownPacket(PacketBuffer buffer, int opcode, PacketFactory packetFactory) {
        LOGGER.warn("Unknown Packet ({}) : {} - {}", packetFactory, toHexString(opcode), CommonUtil.printData(buffer.expose()));
    }

    private PacketFactory getPacketFactory(int opcode, PacketBuffer buffer) {
        IncomingPackets packetFactory = IncomingPackets.PACKET_ARRAY[opcode];
        if(nonNull(packetFactory) && packetFactory.hasExtension()) {
            return packetFactory.handleExtension(buffer);
        }
        return packetFactory;
    }
}
