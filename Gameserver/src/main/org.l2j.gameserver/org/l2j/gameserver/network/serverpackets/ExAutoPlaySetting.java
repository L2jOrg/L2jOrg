package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author JoeAlisson
 */
public class ExAutoPlaySetting extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_AUTOPLAY_SETTING);
        writeShort(13); // size
        writeByte(1); // active ?
        writeByte(1); // pickup ?
        writeShort(1); // nextTarget Mode
        writeByte(0); // near target ?
        writeInt(80); // auto potion hp percent
        writeByte(0); // manner mode
    }
}
