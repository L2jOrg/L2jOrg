package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class FriendAddRequest extends IClientOutgoingPacket {
    private final String _requestorName;

    /**
     * @param requestorName
     */
    public FriendAddRequest(String requestorName) {
        _requestorName = requestorName;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.FRIEND_ADD_REQUEST.writeId(packet);

        packet.put((byte) 0x01);
        writeString(_requestorName, packet);
    }

    @Override
    protected int size(L2GameClient client) {
        return 8 + _requestorName.length() * 2;
    }
}
