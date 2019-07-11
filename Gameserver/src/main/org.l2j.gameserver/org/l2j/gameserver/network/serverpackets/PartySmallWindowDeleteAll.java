package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class PartySmallWindowDeleteAll extends ServerPacket {
    public static final PartySmallWindowDeleteAll STATIC_PACKET = new PartySmallWindowDeleteAll();

    private PartySmallWindowDeleteAll() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.PARTY_SMALL_WINDOW_DELETE_ALL);
    }

}
