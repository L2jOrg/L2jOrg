package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class SocialAction extends ServerPacket {
    // TODO: Enum
    public static final int LEVEL_UP = 2122;

    private final int _charObjId;
    private final int _actionId;

    public SocialAction(int objectId, int actionId) {
        _charObjId = objectId;
        _actionId = actionId;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.SOCIAL_ACTION);

        writeInt(_charObjId);
        writeInt(_actionId);
        writeInt(0x00); // TODO: Find me!
    }

}
