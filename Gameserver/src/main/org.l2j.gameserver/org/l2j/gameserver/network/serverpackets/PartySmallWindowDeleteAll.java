package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class PartySmallWindowDeleteAll extends IClientOutgoingPacket {
    public static final PartySmallWindowDeleteAll STATIC_PACKET = new PartySmallWindowDeleteAll();

    private PartySmallWindowDeleteAll() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PARTY_SMALL_WINDOW_DELETE_ALL.writeId(packet);
    }

    @Override
    protected int size(L2GameClient client) {
        return 5;
    }
}
