package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author Gnacik
 */
public class ExClosePartyRoom extends ServerPacket {
    public static final ExClosePartyRoom STATIC_PACKET = new ExClosePartyRoom();

    private ExClosePartyRoom() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_DISMISS_PARTY_ROOM);
    }

}