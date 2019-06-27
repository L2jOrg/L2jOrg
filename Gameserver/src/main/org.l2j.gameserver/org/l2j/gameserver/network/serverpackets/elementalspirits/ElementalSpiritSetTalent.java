package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.network.L2GameClient;

import static org.l2j.gameserver.network.ServerPacketId.EX_ELEMENTAL_SPIRIT_SET_TALENT;

public class ElementalSpiritSetTalent extends UpdateElementalSpiritPacket {


    public ElementalSpiritSetTalent(byte type, boolean result) {
        super(type, result);
    }

    @Override
    protected void writeImpl(L2GameClient client) {
        writeId(EX_ELEMENTAL_SPIRIT_SET_TALENT);
        writeUpdate(client);
    }
}
