package org.l2j.gameserver.network.clientpackets.elementalspirits;

import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritInfo;

public class ExElementalSpiritChangeType extends ClientPacket {

    private byte element;
    private byte type;

    @Override
    protected void readImpl() throws Exception {
        type = readByte();
        element = readByte(); /* 1 - Fire, 2 - Water, 3 - Wind, 4 Earth */
    }

    @Override
    protected void runImpl() {
        client.getActiveChar().changeElementalSpirit(element);
        client.sendPacket(new ElementalSpiritInfo(element, type));
        client.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_WILL_BE_YOUR_ATTRIBUTE_ATTACK_FROM_NOW_ON).addElementalSpirit(element));
    }
}
