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

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.data.xml.impl.ItemCrystallizationData;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemChanceHolder;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.item.type.CrystalType;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * This class ...
 *
 * @version $Revision: 1.2.2.3.2.5 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestCrystallizeItem extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestCrystallizeItem.class);
    private int _objectId;
    private long _count;

    @Override
    public void readImpl() {
        _objectId = readInt();
        _count = readLong();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();

        if (activeChar == null) {
            LOGGER.debug("RequestCrystalizeItem: activeChar was null");
            return;
        }

        if (_count <= 0) {
            GameUtils.handleIllegalPlayerAction(activeChar, "[RequestCrystallizeItem] count <= 0! ban! oid: " + _objectId + " owner: " + activeChar.getName());
            return;
        }

        if ((activeChar.getPrivateStoreType() != PrivateStoreType.NONE) || !activeChar.isInCrystallize()) {
            client.sendPacket(SystemMessageId.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }

        final int skillLevel = activeChar.getSkillLevel(CommonSkill.CRYSTALLIZE.getId());
        if (skillLevel <= 0) {
            client.sendPacket(SystemMessageId.YOU_MAY_NOT_CRYSTALLIZE_THIS_ITEM_YOUR_CRYSTALLIZATION_SKILL_LEVEL_IS_TOO_LOW);
            client.sendPacket(ActionFailed.STATIC_PACKET);
            if ((activeChar.getRace() != Race.DWARF) && (activeChar.getClassId().getId() != 117) && (activeChar.getClassId().getId() != 55)) {
                LOGGER.info("Player {} used crystalize with classid: {}", activeChar, activeChar.getClassId().getId());
            }
            return;
        }

        final PlayerInventory inventory = activeChar.getInventory();
        if (inventory != null) {
            final Item item = inventory.getItemByObjectId(_objectId);
            if ((item == null) || item.isHeroItem()) {
                client.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
            if (_count > item.getCount()) {
                _count = activeChar.getInventory().getItemByObjectId(_objectId).getCount();
            }
        }

        final Item itemToRemove = activeChar.getInventory().getItemByObjectId(_objectId);
        if ((itemToRemove == null) || itemToRemove.isTimeLimitedItem()) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (!itemToRemove.getTemplate().isCrystallizable() || (itemToRemove.getTemplate().getCrystalCount() <= 0) || (itemToRemove.getTemplate().getCrystalType() == CrystalType.NONE)) {
            client.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_CRYSTALLIZED);
            return;
        }

        if (activeChar.getInventory().isBlocked(itemToRemove)) {
            client.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_CRYSTALLIZED);
            return;
        }

        // Check if the char can crystallize items and return if false;
        boolean canCrystallize = true;

        switch (itemToRemove.getTemplate().getCrystalType()) {
            case D: {
                if (skillLevel < 1) {
                    canCrystallize = false;
                }
                break;
            }
            case C: {
                if (skillLevel < 2) {
                    canCrystallize = false;
                }
                break;
            }
            case B: {
                if (skillLevel < 3) {
                    canCrystallize = false;
                }
                break;
            }
            case A: {
                if (skillLevel < 4) {
                    canCrystallize = false;
                }
                break;
            }
            case S: {
                if (skillLevel < 5) {
                    canCrystallize = false;
                }
                break;
            }
        }

        if (!canCrystallize) {
            client.sendPacket(SystemMessageId.YOU_MAY_NOT_CRYSTALLIZE_THIS_ITEM_YOUR_CRYSTALLIZATION_SKILL_LEVEL_IS_TOO_LOW);
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final List<ItemChanceHolder> crystallizationRewards = ItemCrystallizationData.getInstance().getCrystallizationRewards(itemToRemove);
        if ((crystallizationRewards == null) || crystallizationRewards.isEmpty()) {
            activeChar.sendPacket(SystemMessageId.CRYSTALLIZATION_CANNOT_BE_PROCEEDED_BECAUSE_THERE_ARE_NO_ITEMS_REGISTERED);
            return;
        }

        // activeChar.setInCrystallize(true);

        // unequip if needed
        SystemMessage sm;
        if (itemToRemove.isEquipped()) {
            var unequiped = activeChar.getInventory().unEquipItemInSlotAndRecord(InventorySlot.fromId(itemToRemove.getLocationSlot()));
            final InventoryUpdate iu = new InventoryUpdate();
            for (Item item : unequiped) {
                iu.addModifiedItem(item);
            }
            activeChar.sendInventoryUpdate(iu);

            if (itemToRemove.getEnchantLevel() > 0) {
                sm = SystemMessage.getSystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED);
                sm.addInt(itemToRemove.getEnchantLevel());
                sm.addItemName(itemToRemove);
            } else {
                sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED);
                sm.addItemName(itemToRemove);
            }
            client.sendPacket(sm);
        }

        // remove from inventory
        final Item removedItem = activeChar.getInventory().destroyItem("Crystalize", _objectId, _count, activeChar, null);

        final InventoryUpdate iu = new InventoryUpdate();
        iu.addRemovedItem(removedItem);
        activeChar.sendInventoryUpdate(iu);

        for (ItemChanceHolder holder : crystallizationRewards) {
            final double rand = Rnd.nextDouble() * 100;
            if (rand < holder.getChance()) {
                // add crystals
                final Item createdItem = activeChar.getInventory().addItem("Crystalize", holder.getId(), holder.getCount(), activeChar, activeChar);

                sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1_S);
                sm.addItemName(createdItem);
                sm.addLong(holder.getCount());
                client.sendPacket(sm);
            }
        }

        sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_CRYSTALLIZED);
        sm.addItemName(removedItem);
        client.sendPacket(sm);

        activeChar.broadcastUserInfo();

        activeChar.setInCrystallize(false);
    }
}
