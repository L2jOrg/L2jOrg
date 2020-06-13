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

import io.github.joealisson.primitive.IntCollection;
import org.l2j.commons.util.StreamUtil;
import org.l2j.gameserver.api.elemental.ElementalSpirit;
import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.enums.InventoryBlockType;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritEvolution;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.network.SystemMessageId.*;

public class ExElementalSpiritEvolution extends ClientPacket {

    private byte type;

    @Override
    protected void readImpl() throws Exception {
        type = readByte();
    }

    @Override
    protected void runImpl()  {
        var player = client.getPlayer();
        var spirit = player.getElementalSpirit(ElementalType.of(type));

        if(isNull(spirit)) {
            client.sendPacket(NO_SPIRITS_ARE_AVAILABLE);
            return;
        }

        var canEvolve = checkConditions(player, spirit);

        if(canEvolve) {
            spirit.upgrade();
            client.sendPacket(SystemMessage.getSystemMessage(S1_EVOLVED_TO_S2_STAR).addElementalSpirit(type).addInt(spirit.getStage()));
            var userInfo = new UserInfo(player);
            userInfo.addComponentType(UserInfoType.SPIRITS);
            client.sendPacket(userInfo);
        }
         client.sendPacket(new ElementalSpiritEvolution(type, canEvolve));
    }

    private boolean checkConditions(Player player, ElementalSpirit spirit) {
        var noMeetConditions = false;
        if(noMeetConditions = player.getPrivateStoreType() != PrivateStoreType.NONE) {
            client.sendPacket(CANNOT_EVOLVE_ABSORB_EXTRACT_WHILE_USING_THE_PRIVATE_STORE_WORKSHOP);
        } else if(noMeetConditions = player.isInBattle()) {
            client.sendPacket(CANNOT_EVOLVE_DURING_BATTLE);
        } else if(noMeetConditions = !spirit.canEvolve()) {
            client.sendPacket(SPIRIT_CANNOT_BE_EVOLVED);
        } else if(noMeetConditions = !consumeEvolveItems(player, spirit)) {
            client.sendPacket(YOU_DO_NOT_HAVE_THE_MATERIALS_REQUIRED_TO_EVOLVE);
        }
        return !noMeetConditions;
    }

    private boolean consumeEvolveItems(Player player, ElementalSpirit spirit) {
        var inventory = player.getInventory();
        try {
            IntCollection blocked  = StreamUtil.collectToSet(spirit.getItemsToEvolve().stream().mapToInt(ItemHolder::getId));
            inventory.setInventoryBlock(blocked, InventoryBlockType.BLACKLIST);
            for (ItemHolder itemHolder : spirit.getItemsToEvolve()) {
                if(inventory.getInventoryItemCount(itemHolder.getId(), -1) < itemHolder.getCount()) {
                    return false;
                }
            }

            for (ItemHolder itemHolder : spirit.getItemsToEvolve()) {
                player.destroyItemByItemId("Evolve", itemHolder.getId(), itemHolder.getCount(), player, true);
            }
            return true;
        } finally {
            inventory.unblock();
        }
    }

}
