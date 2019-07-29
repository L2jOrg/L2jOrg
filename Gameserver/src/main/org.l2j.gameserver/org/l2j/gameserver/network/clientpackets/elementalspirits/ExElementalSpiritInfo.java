package org.l2j.gameserver.network.clientpackets.elementalspirits;

import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritInfo;

public class ExElementalSpiritInfo extends ClientPacket {

    private byte type;

    public void readImpl() {
        this.type = readByte();
    }

    public void runImpl() {
        client.sendPacket(new ElementalSpiritInfo((byte) client.getPlayer().getActiveElementalSpiritType(), type));
    }

}