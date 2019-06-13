package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import static org.l2j.gameserver.network.OutgoingPackets.EX_ELEMENTAL_SPIRIT_ABSORB_INFO;

public class ExElementalSpiritAbsorbInfo extends IClientOutgoingPacket {

    @Override
    protected void writeImpl(L2GameClient client) throws Exception {
        writeId(EX_ELEMENTAL_SPIRIT_ABSORB_INFO);
        writeBytes(new byte[] {1, 2, 3, 4, 5, 6});
    }
}
