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

import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.model.item.container.ItemContainer;
import org.l2j.gameserver.model.item.container.PlayerFreight;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author -Wooden-
 * @author UnAfraid Thanks mrTJO
 */
public class RequestPackageSend extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestPackageSend.class);
    private static final int BATCH_LENGTH = 12; // length of the one item

    private ItemHolder[] _items = null;
    private int _objectId;

    @Override
    public void readImpl() throws InvalidDataPacketException {
        _objectId = readInt();
        final int count = readInt();
        if (count <= 0 || count > CharacterSettings.maxItemInPacket() || count * BATCH_LENGTH != available()) {
            throw new InvalidDataPacketException();
        }

        _items = new ItemHolder[count];
        for (int i = 0; i < count; i++) {
            final int objId = readInt();
            final long cnt = readLong();
            if ((objId < 1) || (cnt < 0)) {
                _items = null;
                throw new InvalidDataPacketException();
            }

            _items[i] = new ItemHolder(objId, cnt);
        }
    }

    @Override
    public void runImpl() {
        if (!client.getFloodProtectors().getTransaction().tryPerformAction("deposit")) {
            return;
        }

        final var player = client.getPlayer();
        if (player == null || !player.getAccountChars().containsKey(_objectId)) {
            return;
        }

        if (!canSendPackage(player)) {
            return;
        }

        final int fee = _items.length * CharacterSettings.freightPrice();
        long currentAdena = player.getAdena();
        int slots = 0;

        final ItemContainer warehouse = new PlayerFreight(_objectId);
        for (ItemHolder holder : _items) {
            final var item = itemRequestToItem(player, warehouse, holder);
            if(item == null) {
                return;
            }

            // Calculate needed adena and slots
            if (item.getId() == CommonItem.ADENA) {
                currentAdena -= holder.getCount();
            } else if (!item.isStackable()) {
                slots += holder.getCount();
            } else if (warehouse.getItemByItemId(item.getId()) == null) {
                slots++;
            }
        }

        if (!canDoTransaction(player, fee, currentAdena, slots, warehouse)) {
            return;
        }

        doTransaction(player, warehouse);
    }

    private void doTransaction(Player player, ItemContainer warehouse) {
        final var playerIU =  new InventoryUpdate();
        for (ItemHolder i : _items) {
            // Check validity of requested item
            final Item oldItem = player.checkItemManipulation(i.getId(), i.getCount(), "deposit");
            if (oldItem == null) {
                LOGGER.warn("Error depositing a warehouse object for char " + player.getName() + " (olditem == null)");
                warehouse.deleteMe();
                return;
            }

            final Item newItem = player.getInventory().transferItem("Trade", i.getId(), i.getCount(), warehouse, player, null);
            if (newItem == null) {
                LOGGER.warn("Error depositing a warehouse object for char " + player.getName() + " (newitem == null)");
                continue;
            }

            if ((oldItem.getCount() > 0) && (oldItem != newItem)) {
                playerIU.addModifiedItem(oldItem);
            } else {
                playerIU.addRemovedItem(oldItem);
            }

            // Remove item objects from the world.
            World.getInstance().removeObject(oldItem);
            World.getInstance().removeObject(newItem);
        }

        warehouse.deleteMe();
        player.sendInventoryUpdate(playerIU);
    }

    private boolean canDoTransaction(Player player, int fee, long currentAdena, int slots, ItemContainer warehouse) {
        if (!warehouse.validateCapacity(slots)) {
            player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            warehouse.deleteMe();
            return false;
        }

        if (currentAdena < fee || !player.reduceAdena(warehouse.getName(), fee, null, false)) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
            warehouse.deleteMe();
            return false;
        }
        return true;
    }

    private Item itemRequestToItem(Player player, ItemContainer warehouse, ItemHolder holder) {
        final var item = player.checkItemManipulation(holder.getId(), holder.getCount(), "freight");
        if (item == null) {
            LOGGER.warn("Error depositing a warehouse object for {} (validity check)",  player);
            warehouse.deleteMe();
            return null;
        } else if (!item.isFreightable()) {
            warehouse.deleteMe();
            return null;
        }
        return item;
    }

    private boolean canSendPackage(Player player) {
        if (player.hasItemRequest()) {
            GameUtils.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to use enchant Exploit!");
            return false;
        }

        if (player.getActiveTradeList() != null) {
            return false;
        }

        return player.getReputation() >= 0 || CharacterSettings.canPkUseWareHouse();
    }

}
