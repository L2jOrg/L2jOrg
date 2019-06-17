package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class FriendAddRequest extends ServerPacket {
    private final String _requestorName;

    /**
     * @param requestorName
     */
    public FriendAddRequest(String requestorName) {
        _requestorName = requestorName;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.FRIEND_ADD_REQUEST);

        writeByte((byte) 0x01);
        writeString(_requestorName);
    }

}
