package org.l2j.gameserver.network.clientpackets.elementalspirits;

import org.l2j.gameserver.data.elemental.ElementalSpiritManager;
import org.l2j.gameserver.data.elemental.ElementalType;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritSetTalent;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.network.SystemMessageId.RESET_THE_SELECTED_SPIRIT_S_CHARACTERISTICS_SUCCESSFULLY;
import static org.l2j.gameserver.network.SystemMessageId.UNABLE_TO_RESET_SPIRIT_ATTRIBUTE_DURING_BATTLE;

public class ExElementalInitTalent extends ClientPacket {

    private byte type;

    @Override
    protected void readImpl() throws Exception {
        type =  readByte();
    }

    @Override
    protected void runImpl() {
        var player = client.getActiveChar();

       if(player.isInBattle()) {
           client.sendPacket(SystemMessage.getSystemMessage(UNABLE_TO_RESET_SPIRIT_ATTRIBUTE_DURING_BATTLE));
           client.sendPacket(new ElementalSpiritSetTalent(type, false));
           return;
       }

        var spirit = player.getElementalSpirit(ElementalType.of(type));

        if(nonNull(spirit) && player.reduceAdena("Talent", ElementalSpiritManager.TALENT_INIT_FEE, player, true)) {
            spirit.resetCharacteristics();
            client.sendPacket(SystemMessage.getSystemMessage(RESET_THE_SELECTED_SPIRIT_S_CHARACTERISTICS_SUCCESSFULLY));
            client.sendPacket(new ElementalSpiritSetTalent(type, true));
        } else {
            client.sendPacket(new ElementalSpiritSetTalent(type, false));
        }


    }
}
