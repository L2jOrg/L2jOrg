package org.l2j.gameserver.network.clientpackets.elementalspirits;

import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritEvolutionInfo;

public class ExElementalSpiritEvolutionInfo extends ClientPacket {

    private byte id;

    @Override
    protected void readImpl() throws Exception {
        id = readByte();

    }

    @Override
    protected void runImpl()  {
        client.sendPacket(new ElementalSpiritEvolutionInfo(id));
    }
}
