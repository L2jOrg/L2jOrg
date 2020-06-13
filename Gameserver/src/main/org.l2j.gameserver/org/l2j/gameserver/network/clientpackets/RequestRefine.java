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
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.VariationInstance;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.options.Variation;
import org.l2j.gameserver.model.options.VariationFee;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExVariationResult;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;

/**
 * Format:(ch) dddd
 *
 * @author -Wooden-
 */
public final class RequestRefine extends AbstractRefinePacket {
    private int _targetItemObjId;
    private int _mineralItemObjId;
    private int _feeItemObjId;
    private long _feeCount;

    @Override
    public void readImpl() {
        _targetItemObjId = readInt();
        _mineralItemObjId = readInt();
        _feeItemObjId = readInt();
        _feeCount = readLong();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        final Item targetItem = activeChar.getInventory().getItemByObjectId(_targetItemObjId);
        if (targetItem == null) {
            return;
        }

        final Item mineralItem = activeChar.getInventory().getItemByObjectId(_mineralItemObjId);
        if (mineralItem == null) {
            return;
        }

        final Item feeItem = activeChar.getInventory().getItemByObjectId(_feeItemObjId);
        if (feeItem == null) {
            return;
        }

        final VariationFee fee = VariationData.getInstance().getFee(targetItem.getId(), mineralItem.getId());
        if (!isValid(activeChar, targetItem, mineralItem, feeItem, fee)) {
            activeChar.sendPacket(new ExVariationResult(0, 0, false));
            activeChar.sendPacket(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
            return;
        }

        if (_feeCount != fee.getItemCount()) {
            activeChar.sendPacket(new ExVariationResult(0, 0, false));
            activeChar.sendPacket(SystemMessageId.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
            return;
        }

        final Variation variation = VariationData.getInstance().getVariation(mineralItem.getId());
        if (variation == null) {
            activeChar.sendPacket(new ExVariationResult(0, 0, false));
            return;
        }

        final VariationInstance augment = VariationData.getInstance().generateRandomVariation(variation, targetItem);
        if (augment == null) {
            activeChar.sendPacket(new ExVariationResult(0, 0, false));
            return;
        }

        // unequip item
        final InventoryUpdate iu = new InventoryUpdate();
        if (targetItem.isEquipped()) {
            var unequiped = activeChar.getInventory().unEquipItemInSlotAndRecord(InventorySlot.fromId(targetItem.getLocationSlot()));
            for (Item itm : unequiped) {
                iu.addModifiedItem(itm);
            }
            activeChar.broadcastUserInfo();
        }

        // consume the life stone
        if (!activeChar.destroyItem("RequestRefine", mineralItem, 1, null, false)) {
            return;
        }

        // consume the gemstones
        if (!activeChar.destroyItem("RequestRefine", feeItem, _feeCount, null, false)) {
            return;
        }

        targetItem.setAugmentation(augment, true);
        activeChar.sendPacket(new ExVariationResult(augment.getOption1Id(), augment.getOption2Id(), true));

        iu.addModifiedItem(targetItem);
        activeChar.sendInventoryUpdate(iu);
    }
}
