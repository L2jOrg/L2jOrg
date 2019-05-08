package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class TutorialEnableClientEvent extends IClientOutgoingPacket {
    private int _eventId = 0;

    public TutorialEnableClientEvent(int event) {
        _eventId = event;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.TUTORIAL_ENABLE_CLIENT_EVENT.writeId(packet);

        packet.putInt(_eventId);
    }

    @Override
    protected int size(L2GameClient client) {
        return 9;
    }
}
