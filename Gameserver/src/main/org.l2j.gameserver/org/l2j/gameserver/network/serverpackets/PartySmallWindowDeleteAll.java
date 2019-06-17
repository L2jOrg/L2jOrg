package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class PartySmallWindowDeleteAll extends ServerPacket {
    public static final PartySmallWindowDeleteAll STATIC_PACKET = new PartySmallWindowDeleteAll();

    private PartySmallWindowDeleteAll() {
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.PARTY_SMALL_WINDOW_DELETE_ALL);
    }

}
