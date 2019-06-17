package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import static org.l2j.gameserver.network.ServerPacketId.EX_ELEMENTAL_SPIRIT_GET_EXP;

public class ExElementalSpiritGetExp extends ServerPacket {

    @Override
    protected void writeImpl(L2GameClient client) throws Exception {
        writeId(EX_ELEMENTAL_SPIRIT_GET_EXP);
    }
}
