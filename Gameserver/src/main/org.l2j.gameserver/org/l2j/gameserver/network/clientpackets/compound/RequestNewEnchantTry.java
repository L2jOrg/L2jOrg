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
package org.l2j.gameserver.network.clientpackets.compound;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.data.xml.CombinationItemsManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.CompoundRequest;
import org.l2j.gameserver.model.item.combination.CombinationItem;
import org.l2j.gameserver.model.item.combination.CombinationItemReward;
import org.l2j.gameserver.model.item.combination.CombinationItemType;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.compound.ExEnchantFail;
import org.l2j.gameserver.network.serverpackets.compound.ExEnchantOneFail;
import org.l2j.gameserver.network.serverpackets.compound.ExEnchantSucess;

/**
 * @author UnAfraid
 */
public class RequestNewEnchantTry extends ClientPacket {
    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        } else if (activeChar.isInStoreMode()) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_IN_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            client.sendPacket(ExEnchantOneFail.STATIC_PACKET);
            return;
        } else if (activeChar.isProcessingTransaction() || activeChar.isProcessingRequest()) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_SYSTEM_DURING_TRADING_PRIVATE_STORE_AND_WORKSHOP_SETUP);
            client.sendPacket(ExEnchantOneFail.STATIC_PACKET);
            return;
        }

        final CompoundRequest request = activeChar.getRequest(CompoundRequest.class);
        if ((request == null) || request.isProcessing()) {
            client.sendPacket(ExEnchantFail.STATIC_PACKET);
            return;
        }

        request.setProcessing(true);

        final Item itemOne = request.getItemOne();
        final Item itemTwo = request.getItemTwo();
        if ((itemOne == null) || (itemTwo == null)) {
            client.sendPacket(ExEnchantFail.STATIC_PACKET);
            activeChar.removeRequest(request.getClass());
            return;
        }

        // Lets prevent using same item twice
        if (itemOne.getObjectId() == itemTwo.getObjectId() && itemOne.getCount() < 2) {
            client.sendPacket(new ExEnchantFail(itemOne.getId(), itemTwo.getId()));
            activeChar.removeRequest(request.getClass());
            return;
        }

        final CombinationItem combinationItem = CombinationItemsManager.getInstance().getItemsBySlots(itemOne.getId(), itemTwo.getId());

        // Not implemented or not able to merge!
        if (combinationItem == null) {
            client.sendPacket(new ExEnchantFail(itemOne.getId(), itemTwo.getId()));
            activeChar.removeRequest(request.getClass());
            return;
        }

        final InventoryUpdate iu = new InventoryUpdate();
        iu.addRemovedItem(itemOne);
        iu.addRemovedItem(itemTwo);

        if (activeChar.destroyItem("Compound-Item-One", itemOne, 1, null, true) && activeChar.destroyItem("Compound-Item-Two", itemTwo, 1, null, true)) {
            final double random = (Rnd.nextDouble() * 100);
            final boolean success = random <= combinationItem.getChance();
            final CombinationItemReward rewardItem = combinationItem.getReward(success ? CombinationItemType.ON_SUCCESS : CombinationItemType.ON_FAILURE);
            final Item item = activeChar.addItem("Compound-Result", rewardItem.getId(), rewardItem.getCount(), null, true);

            if (success) {
                client.sendPacket(new ExEnchantSucess(item.getId()));
            } else {
                client.sendPacket(new ExEnchantFail(itemOne.getId(), itemTwo.getId()));
            }
        }

        activeChar.sendInventoryUpdate(iu);
        activeChar.removeRequest(request.getClass());
    }
}
