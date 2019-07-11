package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class AskJoinAlly extends ServerPacket {
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
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.ASK_JOIN_ALLIANCE);

        writeInt(_requestorObjId);
        writeString(null); // Ally Name ?
        writeString(null); // TODO: Find me!
        writeString(_requestorName);
    }

}
