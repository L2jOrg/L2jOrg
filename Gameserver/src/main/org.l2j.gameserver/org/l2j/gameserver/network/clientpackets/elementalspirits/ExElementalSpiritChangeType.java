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
