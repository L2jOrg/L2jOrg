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

import org.l2j.gameserver.api.elemental.ElementalSpirit;
import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritAbsorb;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.network.SystemMessageId.*;

public class ExElementalSpiritAbsorb extends ClientPacket {

    private byte type;
    private int itemId;
    private int amount;

    @Override
    protected void readImpl() throws Exception {
        type = readByte();
        readInt(); //items for now is always 1
        itemId = readInt();
        amount = readInt();

    }

    @Override
    protected void runImpl()  {
        var player = client.getPlayer();

        var spirit = player.getElementalSpirit(ElementalType.of(type));

        if(isNull(spirit)) {
            client.sendPacket(NO_SPIRITS_ARE_AVAILABLE);
            return;
        }

        var absorbItem = spirit.getAbsorbItem(itemId);

        if(isNull(absorbItem)) {
            player.sendPacket(new ElementalSpiritAbsorb(type, false));
            return;
        }

        var canAbsorb = checkConditions(player, spirit);
        if(canAbsorb) {
            client.sendPacket(DRAIN_SUCCESSFUL);
            spirit.addExperience(absorbItem.getExperience() * amount);
            var userInfo = new UserInfo(player);
            userInfo.addComponentType(UserInfoType.SPIRITS);
            client.sendPacket(userInfo);
        }
        client.sendPacket(new ElementalSpiritAbsorb(type, canAbsorb));

    }

    private boolean checkConditions(Player player, ElementalSpirit spirit) {
        var noMeetConditions = false;
        if(noMeetConditions = player.getPrivateStoreType() != PrivateStoreType.NONE) {
            client.sendPacket(CANNOT_EVOLVE_ABSORB_EXTRACT_WHILE_USING_THE_PRIVATE_STORE_WORKSHOP);
        } else if(noMeetConditions = player.isInBattle()) {
            client.sendPacket(CANNOT_DRAIN_DURING_BATTLE);
        } else if(noMeetConditions = (spirit.getLevel() == spirit.getMaxLevel() && spirit.getExperience() == spirit.getExperienceToNextLevel())) {
            client.sendPacket(YOU_HAVE_REACHED_THE_HIGHEST_LEVEL_AND_CANNOT_ABSORB_ANY_FURTHER);
        } else if ( noMeetConditions = (amount < 1 || !player.destroyItemByItemId("Absorb", itemId, amount, player, true))) {
            client.sendPacket(YOU_DO_NOT_HAVE_THE_MATERIALS_REQUIRED_TO_ABSORB);
        }

        return !noMeetConditions;
    }
}
