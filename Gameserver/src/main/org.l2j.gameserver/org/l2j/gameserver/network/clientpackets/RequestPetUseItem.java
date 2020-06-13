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

import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.handler.ItemHandler;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.PetItemList;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RequestPetUseItem extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestPetUseItem.class);
    private int _objectId;


    @Override
    public void readImpl() {
        _objectId = readInt();
        // TODO: implement me properly
        // readLong();
        // readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if ((activeChar == null) || !activeChar.hasPet()) {
            return;
        }

        if (!client.getFloodProtectors().getUseItem().tryPerformAction("pet use item")) {
            return;
        }

        final Pet pet = activeChar.getPet();
        final Item item = pet.getInventory().getItemByObjectId(_objectId);
        if (item == null) {
            return;
        }

        if (!item.getTemplate().isForNpc()) {
            activeChar.sendPacket(SystemMessageId.THIS_PET_CANNOT_USE_THIS_ITEM);
            return;
        }

        if (activeChar.isAlikeDead() || pet.isDead()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
            sm.addItemName(item);
            activeChar.sendPacket(sm);
            return;
        }

        // If the item has reuse time and it has not passed.
        // Message from reuse delay must come from item.
        final int reuseDelay = item.getReuseDelay();
        if (reuseDelay > 0) {
            final long reuse = pet.getItemRemainingReuseTime(item.getObjectId());
            if (reuse > 0) {
                return;
            }
        }

        if (!item.isEquipped() && !item.getTemplate().checkCondition(pet, pet, true)) {
            return;
        }

        useItem(pet, item, activeChar);
    }

    private void useItem(Pet pet, Item item, Player activeChar) {
        if (item.isEquipable()) {
            if (!item.getTemplate().isConditionAttached()) {
                activeChar.sendPacket(SystemMessageId.THIS_PET_CANNOT_USE_THIS_ITEM);
                return;
            }

            if (item.isEquipped()) {
                pet.getInventory().unEquipItemInSlot(InventorySlot.fromId(item.getLocationSlot()));
            } else {
                pet.getInventory().equipItem(item);
            }

            activeChar.sendPacket(new PetItemList(pet.getInventory().getItems()));
            pet.updateAndBroadcastStatus(1);
        } else {
            final IItemHandler handler = ItemHandler.getInstance().getHandler(item.getEtcItem());
            if (handler != null) {
                if (handler.useItem(pet, item, false)) {
                    final int reuseDelay = item.getReuseDelay();
                    if (reuseDelay > 0) {
                        activeChar.addTimeStampItem(item, reuseDelay);
                    }
                    activeChar.sendPacket(new PetItemList(pet.getInventory().getItems()));
                    pet.updateAndBroadcastStatus(1);
                }
            } else {
                activeChar.sendPacket(SystemMessageId.THIS_PET_CANNOT_USE_THIS_ITEM);
                LOGGER.warn("No item handler registered for itemId: " + item.getId());
            }
        }
    }
}
