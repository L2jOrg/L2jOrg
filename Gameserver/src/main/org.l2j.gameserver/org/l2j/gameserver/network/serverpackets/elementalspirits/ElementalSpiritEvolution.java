package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.network.L2GameClient;

import static org.l2j.gameserver.network.ServerPacketId.EX_ELEMENTAL_SPIRIT_EVOLUTION;

public class ElementalSpiritEvolution extends UpdateElementalSpiritPacket {

    public ElementalSpiritEvolution(byte type, boolean evolved) {
        super(type, evolved);
    }

    @Override
    protected void writeImpl(L2GameClient client) {
        writeId(EX_ELEMENTAL_SPIRIT_EVOLUTION);
        writeUpdate(client);
    }
}
