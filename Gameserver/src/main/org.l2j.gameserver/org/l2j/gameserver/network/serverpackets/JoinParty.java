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
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.JOIN_PARTY);

        writeInt(_response);
        writeInt(0x00); // TODO: Find me!
    }

}
