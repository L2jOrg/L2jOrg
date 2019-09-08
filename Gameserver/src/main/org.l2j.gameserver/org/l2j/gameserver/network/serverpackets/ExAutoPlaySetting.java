package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ExAutoPlaySetting extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_AUTOPLAY_SETTING);
        writeShort(13);
        writeByte(0);
        writeByte(1);
        writeByte(1);
        writeShort(0);
        writeInt(80);
        writeByte(1);
    }
}
