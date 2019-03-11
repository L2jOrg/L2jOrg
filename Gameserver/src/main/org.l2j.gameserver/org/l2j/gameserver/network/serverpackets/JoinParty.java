package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class JoinParty extends IClientOutgoingPacket {
    private final int _response;

    public JoinParty(int response) {
        _response = response;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.JOIN_PARTY.writeId(packet);

        packet.putInt(_response);
        packet.putInt(0x00); // TODO: Find me!
    }
}
