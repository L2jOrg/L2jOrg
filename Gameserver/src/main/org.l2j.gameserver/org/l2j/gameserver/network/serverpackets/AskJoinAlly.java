package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class AskJoinAlly extends IClientOutgoingPacket {
    private final String _requestorName;
    private final int _requestorObjId;

    /**
     * @param requestorObjId
     * @param requestorName
     */
    public AskJoinAlly(int requestorObjId, String requestorName) {
        _requestorName = requestorName;
        _requestorObjId = requestorObjId;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.ASK_JOIN_ALLIANCE.writeId(packet);

        packet.putInt(_requestorObjId);
        writeString(null, packet); // Ally Name ?
        writeString(null, packet); // TODO: Find me!
        writeString(_requestorName, packet);
    }
}
