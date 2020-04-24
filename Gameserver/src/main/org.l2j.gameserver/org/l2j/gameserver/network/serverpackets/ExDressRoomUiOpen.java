package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

public class ExDressRoomUiOpen extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client) throws Exception {
        writeId(ServerExPacketId.EX_COMPLETED_DAILY_QUEST_LIST);
        writeInt(0);
    }
}
