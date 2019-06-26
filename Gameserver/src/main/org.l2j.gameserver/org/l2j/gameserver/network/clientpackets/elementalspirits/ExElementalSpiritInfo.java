package org.l2j.gameserver.network.clientpackets.elementalspirits;

import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritInfo;

public class ExElementalSpiritInfo extends ClientPacket {

    private byte id;

    public void readImpl() {
        this.id = readByte();
    }

    public void runImpl() {
        client.sendPacket(new ElementalSpiritInfo(id));
    }

}