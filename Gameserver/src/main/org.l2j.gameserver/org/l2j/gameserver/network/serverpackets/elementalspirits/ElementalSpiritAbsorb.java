package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.network.GameClient;

import static org.l2j.gameserver.network.ServerPacketId.EX_ELEMENTAL_SPIRIT_ABSORB;

public class ElementalSpiritAbsorb extends UpdateElementalSpiritPacket {

    public ElementalSpiritAbsorb(byte type, boolean absorbed) {
        super(type, absorbed);
    }

    @Override
    protected void writeImpl(GameClient client)  {
        writeId(EX_ELEMENTAL_SPIRIT_ABSORB);
        writeUpdate(client);
    }
}
