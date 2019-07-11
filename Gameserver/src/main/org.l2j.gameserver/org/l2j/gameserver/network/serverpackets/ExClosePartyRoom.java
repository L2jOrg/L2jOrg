package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Gnacik
 */
public class ExClosePartyRoom extends ServerPacket {
    public static final ExClosePartyRoom STATIC_PACKET = new ExClosePartyRoom();

    private ExClosePartyRoom() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_CLOSE_PARTY_ROOM);
    }

}