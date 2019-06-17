package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Gnacik
 */
public class ExClosePartyRoom extends ServerPacket {
    public static final ExClosePartyRoom STATIC_PACKET = new ExClosePartyRoom();

    private ExClosePartyRoom() {
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_CLOSE_PARTY_ROOM);
    }

}