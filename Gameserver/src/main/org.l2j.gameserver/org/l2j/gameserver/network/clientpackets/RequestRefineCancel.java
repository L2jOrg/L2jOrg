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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.xml.impl.VariationData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExVariationCancelResult;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.util.GameUtils;

/**
 * Format(ch) d
 *
 * @author -Wooden-
 */
public final class RequestRefineCancel extends ClientPacket {
    private int _targetItemObjId;

    @Override
    public void readImpl() {
        _targetItemObjId = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        final Item targetItem = activeChar.getInventory().getItemByObjectId(_targetItemObjId);
        if (targetItem == null) {
            client.sendPacket(ExVariationCancelResult.STATIC_PACKET_FAILURE);
            return;
        }

        if (targetItem.getOwnerId() != activeChar.getObjectId()) {
            GameUtils.handleIllegalPlayerAction(client.getPlayer(), "Warning!! Character " + client.getPlayer().getName() + " of account " + client.getPlayer().getAccountName() + " tryied to augment item that doesn't own.");
            return;
        }

        // cannot remove augmentation from a not augmented item
        if (!targetItem.isAugmented()) {
            client.sendPacket(SystemMessageId.AUGMENTATION_REMOVAL_CAN_ONLY_BE_DONE_ON_AN_AUGMENTED_ITEM);
            client.sendPacket(ExVariationCancelResult.STATIC_PACKET_FAILURE);
            return;
        }

        // get the price
        final long price = VariationData.getInstance().getCancelFee(targetItem.getId(), targetItem.getAugmentation().getMineralId());
        if (price < 0) {
            client.sendPacket(ExVariationCancelResult.STATIC_PACKET_FAILURE);
            return;
        }

        // try to reduce the players adena
        if (!activeChar.reduceAdena("RequestRefineCancel", price, targetItem, true)) {
            client.sendPacket(ExVariationCancelResult.STATIC_PACKET_FAILURE);
            client.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
            return;
        }

        // unequip item
        if (targetItem.isEquipped()) {
            activeChar.disarmWeapons();
        }

        // remove the augmentation
        targetItem.removeAugmentation();

        // send ExVariationCancelResult
        client.sendPacket(ExVariationCancelResult.STATIC_PACKET_SUCCESS);

        // send inventory update
        InventoryUpdate iu = new InventoryUpdate();
        iu.addModifiedItem(targetItem);
        activeChar.sendInventoryUpdate(iu);
    }
}
