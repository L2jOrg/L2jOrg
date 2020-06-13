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

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.handler.AdminCommandHandler;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.l2j.gameserver.util.GameUtils.isItem;

/**
 * This class ...
 *
 * @version $Revision: 1.7.2.4.2.6 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestDestroyItem extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestDestroyItem.class);
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
            return;
        }

        if (_count <= 0) {
            if (_count < 0) {
                GameUtils.handleIllegalPlayerAction(activeChar, "[RequestDestroyItem] Character " + activeChar.getName() + " of account " + activeChar.getAccountName() + " tried to destroy item with oid " + _objectId + " but has count < 0!");
            }
            return;
        }

        if (!client.getFloodProtectors().getTransaction().tryPerformAction("destroy")) {
            activeChar.sendMessage("You are destroying items too fast.");
            return;
        }

        long count = _count;

        if (activeChar.isProcessingTransaction() || (activeChar.getPrivateStoreType() != PrivateStoreType.NONE)) {
            client.sendPacket(SystemMessageId.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }

        if (activeChar.hasItemRequest()) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_DESTROY_OR_CRYSTALLIZE_ITEMS_WHILE_ENCHANTING_ATTRIBUTES);
            return;
        }

        final Item itemToRemove = activeChar.getInventory().getItemByObjectId(_objectId);

        // if we can't find the requested item, its actually a cheat
        if (itemToRemove == null) {
            // gm can destroy other player items
            if (activeChar.isGM()) {
                final WorldObject obj = World.getInstance().findObject(_objectId);
                if (isItem(obj)) {
                    if (_count > ((Item) obj).getCount()) {
                        count = ((Item) obj).getCount();
                    }
                    AdminCommandHandler.getInstance().useAdminCommand(activeChar, "admin_delete_item " + _objectId + " " + count, true);
                }
                return;
            }

            client.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_DESTROYED);
            return;
        }

        // Cannot discard item that the skill is consuming
        if (activeChar.isCastingNow(s -> s.getSkill().getItemConsumeId() == itemToRemove.getId())) {
            client.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_DESTROYED);
            return;
        }

        final int itemId = itemToRemove.getId();

        if (!Config.DESTROY_ALL_ITEMS && ((!activeChar.canOverrideCond(PcCondOverride.DESTROY_ALL_ITEMS) && !itemToRemove.isDestroyable()))) {
            if (itemToRemove.isHeroItem()) {
                client.sendPacket(SystemMessageId.HERO_WEAPONS_CANNOT_BE_DESTROYED);
            } else {
                client.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_DESTROYED);
            }
            return;
        }

        if (!itemToRemove.isStackable() && (count > 1)) {
            GameUtils.handleIllegalPlayerAction(activeChar, "[RequestDestroyItem] Character " + activeChar.getName() + " of account " + activeChar.getAccountName() + " tried to destroy a non-stackable item with oid " + _objectId + " but has count > 1!");
            return;
        }

        if (activeChar.getInventory().isBlocked(itemToRemove)) {
            activeChar.sendMessage("You cannot use this item.");
            return;
        }

        if (_count > itemToRemove.getCount()) {
            count = itemToRemove.getCount();
        }

        if (itemToRemove.getTemplate().isPetItem()) {
            final Summon pet = activeChar.getPet();
            if ((pet != null) && (pet.getControlObjectId() == _objectId)) {
                pet.unSummon(activeChar);
            }

            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement statement = con.prepareStatement("DELETE FROM pets WHERE item_obj_id=?")) {
                statement.setInt(1, _objectId);
                statement.execute();
            } catch (Exception e) {
                LOGGER.warn("could not delete pet objectid: ", e);
            }
        }
        if (itemToRemove.isTimeLimitedItem()) {
            itemToRemove.endOfLife();
        }

        if (itemToRemove.isEquipped()) {
            if (itemToRemove.getEnchantLevel() > 0) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED);
                sm.addInt(itemToRemove.getEnchantLevel());
                sm.addItemName(itemToRemove);
                client.sendPacket(sm);
            } else {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED);
                sm.addItemName(itemToRemove);
                client.sendPacket(sm);
            }

            var unequiped = activeChar.getInventory().unEquipItemInSlotAndRecord(InventorySlot.fromId(itemToRemove.getLocationSlot()));

            final InventoryUpdate iu = new InventoryUpdate();
            for (Item itm : unequiped) {
                iu.addModifiedItem(itm);
            }
            activeChar.sendInventoryUpdate(iu);
        }

        final Item removedItem = activeChar.getInventory().destroyItem("Destroy", itemToRemove, count, activeChar, null);

        if (removedItem == null) {
            return;
        }

        if (!Config.FORCE_INVENTORY_UPDATE) {
            final InventoryUpdate iu = new InventoryUpdate();
            if (removedItem.getCount() == 0) {
                iu.addRemovedItem(removedItem);
            } else {
                iu.addModifiedItem(removedItem);
            }
            activeChar.sendInventoryUpdate(iu);
        } else {
            activeChar.sendItemList();
        }
    }
}
