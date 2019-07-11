package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class CharDeleteSuccess extends ServerPacket {
    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.CHARACTER_DELETE_SUCCESS);
    }

}
