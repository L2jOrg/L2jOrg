package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import static org.l2j.gameserver.network.ServerPacketId.EX_ELEMENTAL_SPIRIT_EXTRACT;

public class ElementalSpiritExtract extends UpdateElementalSpiritPacket {

    public ElementalSpiritExtract(byte type, boolean extracted) {
        super(type, extracted);
    }

    @Override
    protected void writeImpl(L2GameClient client) {
        writeId(EX_ELEMENTAL_SPIRIT_EXTRACT);
        writeUpdate(client);
    }
}
