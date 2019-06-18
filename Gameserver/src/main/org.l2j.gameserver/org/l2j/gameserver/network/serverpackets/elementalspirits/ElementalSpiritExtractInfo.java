package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ElementalSpiritExtractInfo extends ServerPacket {

    @Override
    protected void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_ELEMENTAL_SPIRIT_EXTRACT_INFO);
        writeByte(2); // active elemental spirit
        writeByte(1); // is extract ?
        
        writeByte(2); // cost count

        for (int i = 0; i < 2; i++) { // for each cost count
            writeInt(57); // item id
            writeInt(10000); // item count
        }

        writeInt(57); // result item id
        writeInt(1000); // result item count
    }
}
