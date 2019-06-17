package org.l2j.gameserver.network;

import io.github.joealisson.mmocore.PacketBuffer;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

interface PacketFactory {

    Supplier<ClientPacket> NULL_PACKET_SUPLIER = () -> null;
    PacketFactory NULLABLE_PACKET_FACTORY = () -> null;

    default int getPacketId() {
        return -1;
    }

    ClientPacket newIncomingPacket();

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
