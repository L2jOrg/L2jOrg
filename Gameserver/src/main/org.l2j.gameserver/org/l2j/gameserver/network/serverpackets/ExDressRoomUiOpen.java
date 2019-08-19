package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ExDressRoomUiOpen extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client) throws Exception {
        writeId(ServerPacketId.EX_DRESS_ROOM_UI_OPEN);
        writeInt(0);
    }
}
