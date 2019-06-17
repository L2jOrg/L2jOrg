package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ElementalSpiritExtractInfo extends ServerPacket {

    @Override
    protected void writeImpl(L2GameClient client) throws Exception {
        writeId(ServerPacketId.EX_ELEMENTAL_SPIRIT_EXTRACT_INFO);
        writeBytes(new byte[] { 1, 2, 3, 4, 5, 6} );

    }
}
