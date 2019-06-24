package org.l2j.gameserver.network.clientpackets.elementalspirits;

import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritAbsorbInfo;

public class ExElementalSpiritAbsorbInfo extends ClientPacket {

    private byte type;

    @Override
    protected void readImpl() throws Exception {
        readByte(); // unk
        type = readByte();
    }

    @Override
    protected void runImpl() {
        client.sendPacket(new ElementalSpiritAbsorbInfo(type));
    }
}
