package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

public class ElementalSpiritExtractInfo extends IClientOutgoingPacket {

    @Override
    protected void writeImpl(L2GameClient client) throws Exception {
        writeId(OutgoingPackets.EX_ELEMENTAL_SPIRIT_EXTRACT_INFO);
        writeBytes(new byte[] { 1, 2, 3, 4, 5, 6} );

    }
}
