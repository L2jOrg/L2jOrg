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
import org.l2j.gameserver.network.clientpackets.ClientPacket;

import java.util.EnumSet;
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

    default EnumSet<ConnectionState> getConnectionStates() {
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
