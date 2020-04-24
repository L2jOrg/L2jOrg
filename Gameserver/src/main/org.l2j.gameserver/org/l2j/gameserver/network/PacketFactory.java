package org.l2j.gameserver.network;

import io.github.joealisson.mmocore.PacketBuffer;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

import java.util.function.Supplier;

interface PacketFactory {

    Supplier<ClientPacket> NULL_PACKET_SUPLIER = () -> null;
    PacketFactory NULLABLE_PACKET_FACTORY = () -> null;
    DiscardPacket DISCARD_PACKET = new DiscardPacket();
    Supplier<ClientPacket> DISCARD = () -> DISCARD_PACKET;

    default int getPacketId() {
        return -1;
    }

    ClientPacket newIncomingPacket();

    default ConnectionState[] getConnectionStates() {
        return ConnectionState.EMPTY;
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
