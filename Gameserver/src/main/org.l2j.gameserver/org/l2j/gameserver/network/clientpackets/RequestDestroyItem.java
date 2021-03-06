/*
 * Copyright © 2019-2021 L2JOrg
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

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.dao.PetDAO;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.handler.AdminCommandHandler;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;

import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.GameUtils.isItem;

/**
 * @author JoeAlisson
 */
public final class RequestDestroyItem extends ClientPacket {

    private int objectId;
    private long count;

    @Override
    public void readImpl() {
        objectId = readInt();
        count = readLong();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        if (count <= 0) {
            if (count < 0) {
                GameUtils.handleIllegalPlayerAction(player, "[RequestDestroyItem] Player " + player + " tried to destroy item with oid " + objectId + " but has count < 0!");
            }
            return;
        }

        if (!client.getFloodProtectors().getTransaction().tryPerformAction("destroy")) {
            player.sendMessage("You are destroying items too fast.");
            return;
        }

        if (player.isProcessingTransaction() || (player.getPrivateStoreType() != PrivateStoreType.NONE)) {
            client.sendPacket(SystemMessageId.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }

        if (player.hasItemRequest()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_DESTROY_OR_CRYSTALLIZE_ITEMS_WHILE_ENCHANTING_ATTRIBUTES);
            return;
        }

        final Item itemToRemove = player.getInventory().getItemByObjectId(objectId);

        // if we can't find the requested item, its actually a cheat
        if (itemToRemove == null) {
            // gm can destroy other player items
            if (player.isGM()) {
                final WorldObject obj = World.getInstance().findObject(objectId);
                if (isItem(obj)) {
                    if (this.count > ((Item) obj).getCount()) {
                        count = ((Item) obj).getCount();
                    }
                    AdminCommandHandler.getInstance().useAdminCommand(player, "admin_delete_item " + objectId + " " + count, true);
                }
                return;
            }

            client.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_DESTROYED);
            return;
        }

        // Cannot discard item that the skill is consuming
        if (player.isCastingNow(s -> s.getSkill().getItemConsumeId() == itemToRemove.getId())) {
            client.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_DESTROYED);
            return;
        }

        if (!Config.DESTROY_ALL_ITEMS && ((!player.canOverrideCond(PcCondOverride.DESTROY_ALL_ITEMS) && !itemToRemove.isDestroyable()))) {
            if (itemToRemove.isHeroItem()) {
                client.sendPacket(SystemMessageId.HERO_WEAPONS_CANNOT_BE_DESTROYED);
            } else {
                client.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_DESTROYED);
            }
            return;
        }

        if (!itemToRemove.isStackable() && (count > 1)) {
            GameUtils.handleIllegalPlayerAction(player, "[RequestDestroyItem] Character " + player.getName() + " of account " + player.getAccountName() + " tried to destroy a non-stackable item with oid " + objectId + " but has count > 1!");
            return;
        }

        if (player.getInventory().isBlocked(itemToRemove)) {
            player.sendMessage("You cannot use this item.");
            return;
        }

        if (this.count > itemToRemove.getCount()) {
            count = itemToRemove.getCount();
        }

        if (itemToRemove.getTemplate().isPetItem()) {
            final Summon pet = player.getPet();
            if ((pet != null) && (pet.getControlObjectId() == objectId)) {
                pet.unSummon(player);
            }

            getDAO(PetDAO.class).deleteByItem(objectId);
        }
        if (itemToRemove.isTimeLimitedItem()) {
            itemToRemove.endOfLife();
        }

        if (itemToRemove.isEquipped()) {
            if (itemToRemove.getEnchantLevel() > 0) {
                client.sendPacket(getSystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED).addInt(itemToRemove.getEnchantLevel()).addItemName(itemToRemove));
            } else {
                client.sendPacket(getSystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED).addItemName(itemToRemove));
            }

            player.sendPacket(new InventoryUpdate(player.getInventory().unEquipItemInSlotAndRecord(InventorySlot.fromId(itemToRemove.getLocationSlot()))));
        }

        final Item removedItem = player.getInventory().destroyItem("Destroy", itemToRemove, count, player, null);
        if (removedItem == null) {
            return;
        }
        player.sendInventoryUpdate(new InventoryUpdate(removedItem));
    }
}
