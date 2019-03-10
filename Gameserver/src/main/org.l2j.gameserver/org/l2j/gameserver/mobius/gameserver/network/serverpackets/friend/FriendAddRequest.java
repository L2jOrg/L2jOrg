package org.l2j.gameserver.mobius.gameserver.network.serverpackets.friend;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

public class FriendAddRequest extends IClientOutgoingPacket {
    private final String _requestorName;

    public FriendAddRequest(String requestorName) {
        _requestorName = requestorName;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.FRIEND_ADD_REQUEST.writeId(packet);

        packet.put((byte) 0x00);
        writeString(_requestorName, packet);
    }
}
