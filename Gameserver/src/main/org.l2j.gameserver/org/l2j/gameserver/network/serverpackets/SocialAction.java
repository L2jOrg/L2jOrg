package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class SocialAction extends IClientOutgoingPacket {
    // TODO: Enum
    public static final int LEVEL_UP = 2122;

    private final int _charObjId;
    private final int _actionId;

    public SocialAction(int objectId, int actionId) {
        _charObjId = objectId;
        _actionId = actionId;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.SOCIAL_ACTION.writeId(packet);

        packet.putInt(_charObjId);
        packet.putInt(_actionId);
        packet.putInt(0x00); // TODO: Find me!
    }

    @Override
    protected int size(L2GameClient client) {
        return 17;
    }
}
