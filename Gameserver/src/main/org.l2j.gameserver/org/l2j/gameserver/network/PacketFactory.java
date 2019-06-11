package org.l2j.gameserver.network;

import io.github.joealisson.mmocore.PacketBuffer;
import org.l2j.gameserver.network.clientpackets.IClientIncomingPacket;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

interface PacketFactory {

    Supplier<IClientIncomingPacket> NULL_PACKET_SUPLIER = () -> null;
    PacketFactory NULLABLE_PACKET_FACTORY = () -> null;

    default int getPacketId() {
        return -1;
    }

    IClientIncomingPacket newIncomingPacket();

    default Set<ConnectionState> getConnectionStates() {
        return Collections.emptySet();
    }

    default boolean canHandleState(ConnectionState state) {
        return false;
    }

    default boolean hasExtension() {
        return false;
    }

    default PacketFactory handleExtension(PacketBuffer buffer) {
        return NULLABLE_PACKET_FACTORY;
    }


}
