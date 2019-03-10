package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Gnacik
 */
public class ExClosePartyRoom extends IClientOutgoingPacket {
    public static final ExClosePartyRoom STATIC_PACKET = new ExClosePartyRoom();

    private ExClosePartyRoom() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_CLOSE_PARTY_ROOM.writeId(packet);
    }
}