package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.network.GameClient;

import static org.l2j.gameserver.network.ServerPacketId.EX_ELEMENTAL_SPIRIT_EXTRACT;

public class ElementalSpiritExtract extends UpdateElementalSpiritPacket {

    public ElementalSpiritExtract(byte type, boolean extracted) {
        super(type, extracted);
    }

    @Override
    protected void writeImpl(GameClient client) {
        writeId(EX_ELEMENTAL_SPIRIT_EXTRACT);
        writeUpdate(client);
    }
}
