/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.network.clientpackets.elementalspirits;

import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.engine.elemental.ElementalSpiritEngine;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritSetTalent;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.network.SystemMessageId.*;

public class ExElementalInitTalent extends ClientPacket {

    private byte type;

    @Override
    protected void readImpl() throws Exception {
        type =  readByte();
    }

    @Override
    protected void runImpl() {
        var player = client.getPlayer();

        var spirit = player.getElementalSpirit(ElementalType.of(type));

        if(isNull(spirit)) {
            client.sendPacket(NO_SPIRITS_ARE_AVAILABLE);
            return;
        }

       if(player.isInBattle()) {
           client.sendPacket(SystemMessage.getSystemMessage(CANNOT_RESET_SPIRIT_CHARACTERISTICS_DURING_BATTLE));
           client.sendPacket(new ElementalSpiritSetTalent(type, false));
           return;
       }

        if(player.reduceAdena("Talent", ElementalSpiritEngine.TALENT_INIT_FEE, player, true)) {
            spirit.resetCharacteristics();
            client.sendPacket(SystemMessage.getSystemMessage(RESET_THE_SELECTED_SPIRIT_S_CHARACTERISTICS_SUCCESSFULLY));
            client.sendPacket(new ElementalSpiritSetTalent(type, true));
        } else {
            client.sendPacket(new ElementalSpiritSetTalent(type, false));
        }


    }
}
