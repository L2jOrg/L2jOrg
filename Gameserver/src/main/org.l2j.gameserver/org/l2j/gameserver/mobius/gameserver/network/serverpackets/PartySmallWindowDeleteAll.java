package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class PartySmallWindowDeleteAll extends IClientOutgoingPacket {
    public static final PartySmallWindowDeleteAll STATIC_PACKET = new PartySmallWindowDeleteAll();

    private PartySmallWindowDeleteAll() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PARTY_SMALL_WINDOW_DELETE_ALL.writeId(packet);
    }
}
