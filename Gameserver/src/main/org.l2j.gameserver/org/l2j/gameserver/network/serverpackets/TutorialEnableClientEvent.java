package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class TutorialEnableClientEvent extends ServerPacket {
    private int _eventId = 0;

    public TutorialEnableClientEvent(int event) {
        _eventId = event;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.TUTORIAL_ENABLE_CLIENT_EVENT);

        writeInt(_eventId);
    }

}
