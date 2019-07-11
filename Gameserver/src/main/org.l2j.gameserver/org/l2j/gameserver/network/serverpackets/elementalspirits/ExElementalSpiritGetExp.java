package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import static org.l2j.gameserver.network.ServerPacketId.EX_ELEMENTAL_SPIRIT_GET_EXP;

public class ExElementalSpiritGetExp extends ServerPacket {

    private final long experience;
    private final byte type;

    public ExElementalSpiritGetExp(byte type, long experience) {
        this.type = type;
        this.experience = experience;
    }

    @Override
    protected void writeImpl(GameClient client)  {
        writeId(EX_ELEMENTAL_SPIRIT_GET_EXP);

        writeByte(type);
        writeLong(experience);
    }
}
