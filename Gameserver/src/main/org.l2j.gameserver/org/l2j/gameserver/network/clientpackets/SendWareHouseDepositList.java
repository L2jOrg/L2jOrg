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

import org.l2j.commons.util.StreamUtil;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.InventoryBlockType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.model.item.container.Warehouse;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.item.WarehouseDone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.isNullOrEmpty;
import static org.l2j.gameserver.util.GameUtils.handleIllegalPlayerAction;
import static org.l2j.gameserver.util.GameUtils.isWarehouseManager;

public final class SendWareHouseDepositList extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendWareHouseDepositList.class);
    private static final int BATCH_LENGTH = 12;

    private List<ItemHolder> items = null;

    @Override
    public void readImpl() throws InvalidDataPacketException {
        final int size = readInt();
        if (size <= 0 || size > Config.MAX_ITEM_IN_PACKET || size * BATCH_LENGTH != available()) {
            throw new InvalidDataPacketException();
        }

        items = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            final int objId = readInt();
            final long count = readLong();
            if (objId < 1 || count < 0) {
                items = null;
                throw new InvalidDataPacketException();
            }
            items.add(new ItemHolder(objId, count));
        }
    }

    @Override
    public void runImpl() {
        if (isNullOrEmpty(items)) {
            return;
        }

        final var player = client.getPlayer();
        final var warehouse = player.getActiveWarehouse();
        final var manager = player.getLastFolkNPC();

        var inventory = player.getInventory();
        try {

            inventory.setInventoryBlock(StreamUtil.collectToSet(items.stream().mapToInt(ItemHolder::getId)), InventoryBlockType.BLACKLIST);

            if (isNull(warehouse) || !checkWarehouseManager(manager, player) ||  !checkTransanction(warehouse, manager)) {
                client.sendPacket(new WarehouseDone(false));
                return;
            }

            deposit(player, warehouse, manager, inventory);

            client.sendPacket(new WarehouseDone(true));
            client.sendPacket(SystemMessageId.ITEM_HAS_BEEN_STORED_SUCCESSFULLY);
        } finally {
            inventory.unblock();
        }
    }

    private void deposit(Player player, Warehouse warehouse, Npc manager, PlayerInventory inventory) {
        final var inventoryUpdate = new InventoryUpdate();
        for (ItemHolder i : items) {
            var modifiedItem = inventory.getItemByObjectId(i.getId());
            inventory.transferItem(warehouse.getName(), i.getId(), i.getCount(), warehouse, player, manager);

            if(nonNull(inventory.getItemByObjectId(i.getId()))) {
                inventoryUpdate.addModifiedItem(modifiedItem);
            } else {
                inventoryUpdate.addRemovedItem(modifiedItem);
            }
        }
        player.sendInventoryUpdate(inventoryUpdate);
    }

    private boolean checkWarehouseManager(Npc manager, Player player) {
        return player.isGM() || (isWarehouseManager(manager) && manager.canInteract(player));
    }

    private boolean checkTransanction(Warehouse warehouse, Npc manager) {
        final var player = client.getPlayer();

        if (nonNull(player.getActiveTradeList())) {
            return false;
        }

        if (!warehouse.isPrivate() && !player.getAccessLevel().allowTransaction()) {
            player.sendMessage("Transactions are disabled for your Access Level.");
            return false;
        }

        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE && (player.getReputation() < 0)) {
            return false;
        }

        if (player.hasItemRequest()) {
            handleIllegalPlayerAction(player, "Player " + player + " tried to use enchant Exploit!");
            return false;
        }

        if (!client.getFloodProtectors().getTransaction().tryPerformAction("deposit")) {
            player.sendMessage("You are depositing items too fast.");
            return false;
        }
        return checkItemsAndCharge(warehouse, player, manager);
    }

    private boolean checkItemsAndCharge(Warehouse warehouse, Player player, Npc manager) {
        final long fee = items.size() * 30;
        final var inventory = player.getInventory();
        long currentAdena = inventory.getAdena();
        int slots = 0;

        for (ItemHolder i : items) {
            final Item item = player.checkItemManipulation(i.getId(), i.getCount(), "deposit");
            if (isNull(item)) {
                LOGGER.warn("Error depositing a warehouse object for player {} (validity check)", player);
                return false;
            }

            if (!item.isDepositable(warehouse.getType()) || !inventory.isNotInUse(item)) {
                return false;
            }

            if (item.getId() == CommonItem.ADENA) {
                currentAdena -= i.getCount();
            }

            if (!item.isStackable()) {
                slots += i.getCount();
            } else if (isNull(warehouse.getItemByItemId(item.getId()))) {
                slots++;
            }
        }

        if (!warehouse.validateCapacity(slots)) {
            client.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return false;
        }

        if (currentAdena < fee || !player.reduceAdena(warehouse.getName(), fee, manager, false)) {
            client.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
            return false;
        }
        return true;
    }
}
