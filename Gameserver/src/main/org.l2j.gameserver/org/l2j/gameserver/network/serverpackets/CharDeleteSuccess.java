package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class CharDeleteSuccess extends ServerPacket {
    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.CHARACTER_DELETE_SUCCESS);
    }

}
