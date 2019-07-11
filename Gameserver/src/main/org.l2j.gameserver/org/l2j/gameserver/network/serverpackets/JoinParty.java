package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class JoinParty extends ServerPacket {
    private final int _response;

    public JoinParty(int response) {
        _response = response;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.JOIN_PARTY);

        writeInt(_response);
        writeInt(0x00); // TODO: Find me!
    }

}
