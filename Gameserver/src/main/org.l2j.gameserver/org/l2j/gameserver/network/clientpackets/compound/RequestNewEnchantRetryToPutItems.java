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

import org.l2j.gameserver.data.xml.CombinationItemsManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.CompoundRequest;
import org.l2j.gameserver.model.item.combination.CombinationItem;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.ExEnchantRetryToPutItemFail;
import org.l2j.gameserver.network.serverpackets.ExEnchantRetryToPutItemOk;

/**
 * @author Sdw
 */
public class RequestNewEnchantRetryToPutItems extends ClientPacket {
    private int _firstItemObjectId;
    private int _secondItemObjectId;

    @Override
    public void readImpl() {
        _firstItemObjectId = readInt();
        _secondItemObjectId = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        } else if (activeChar.isInStoreMode()) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_IN_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            client.sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
            return;
        } else if (activeChar.isProcessingTransaction() || activeChar.isProcessingRequest()) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_SYSTEM_DURING_TRADING_PRIVATE_STORE_AND_WORKSHOP_SETUP);
            client.sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
            return;
        }

        final CompoundRequest request = new CompoundRequest(activeChar);
        if (!activeChar.addRequest(request)) {
            client.sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
            return;
        }

        // Make sure player owns first item.
        request.setItemOne(_firstItemObjectId);
        final Item itemOne = request.getItemOne();
        if (itemOne == null) {
            client.sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
            activeChar.removeRequest(request.getClass());
            return;
        }

        // Make sure player owns second item.
        request.setItemTwo(_secondItemObjectId);
        final Item itemTwo = request.getItemTwo();
        if (itemTwo == null) {
            client.sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
            activeChar.removeRequest(request.getClass());
            return;
        }

        final CombinationItem combinationItem = CombinationItemsManager.getInstance().getItemsBySlots(itemOne.getId(), itemTwo.getId());

        // Not implemented or not able to merge!
        if (combinationItem == null) {
            client.sendPacket(ExEnchantRetryToPutItemFail.STATIC_PACKET);
            activeChar.removeRequest(request.getClass());
            return;
        }
        client.sendPacket(ExEnchantRetryToPutItemOk.STATIC_PACKET);
    }
}
