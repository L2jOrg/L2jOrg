package org.l2j.gameserver.network.clientpackets.elementalspirits;

import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritExtractInfo;

public class ExElementalSpiritExtractInfo extends ClientPacket {

    private byte type;

    @Override
    protected void readImpl() throws Exception {
        type = readByte();
    }

    @Override
    protected void runImpl() {
        client.sendPacket(new ElementalSpiritExtractInfo(type));
    }
}
