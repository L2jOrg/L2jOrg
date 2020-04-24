package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

public class ExCostumeShortCutList extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client) throws Exception {
        writeId(ServerExPacketId.EX_COSTUME_SHORTCUT_LIST);
        writeByte(0x01);
        writeInt(0);
    }
}
