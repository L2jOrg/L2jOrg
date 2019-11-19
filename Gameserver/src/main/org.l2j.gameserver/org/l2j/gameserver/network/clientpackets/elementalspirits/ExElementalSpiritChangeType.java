package org.l2j.gameserver.network.clientpackets.elementalspirits;

import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritInfo;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.network.SystemMessageId.NO_SPIRITS_ARE_AVAILABLE;

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
        var player = client.getPlayer();

        if(isNull(player.getElementalSpirit(ElementalType.of(element)))) {
            client.sendPacket(NO_SPIRITS_ARE_AVAILABLE);
            return;
        }

        player.changeElementalSpirit(element);
        client.sendPacket(new ElementalSpiritInfo(element, type));
        client.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_WILL_BE_YOUR_ATTRIBUTE_ATTACK_FROM_NOW_ON).addElementalSpirit(element));
    }
}
