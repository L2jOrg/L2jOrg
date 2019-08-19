package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ExUnk221 extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client) throws Exception {
        writeId(ServerPacketId.EX_UNK_221);
        writeShort(13);
        writeByte(0);
        writeByte(1);
        writeByte(1);
        writeShort(0);
        writeInt(80);
        writeByte(1);
    }
}
