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
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.item.container.ClanWarehouse;
import org.l2j.gameserver.model.item.container.ItemContainer;
import org.l2j.gameserver.model.item.container.PlayerWarehouse;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.settings.ClanSettings;
import org.l2j.gameserver.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SendWareHouseWithDrawList extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendWareHouseWithDrawList.class);
    private static final int BATCH_LENGTH = 12; // length of the one item

    private ItemHolder[] _items = null;

    @Override
    public void readImpl() throws InvalidDataPacketException {
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
        if (!client.getFloodProtectors().getTransaction().tryPerformAction("withdraw")) {
            return;
        }
        
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        final ItemContainer warehouse = player.getActiveWarehouse();
        final Npc manager = player.getLastFolkNPC();
        if (!canPlayerWithdraw(player, warehouse, manager)) {
            return;
        }

        int weight = 0;
        int slots = 0;

        for (ItemHolder i : _items) {
            // Calculate needed slots
            final Item item = warehouse.getItemByObjectId(i.getId());
            if ((item == null) || (item.getCount() < i.getCount())) {
                GameUtils.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to withdraw non-existent item from warehouse.");
                return;
            }

            weight += i.getCount() * item.getTemplate().getWeight();
            if (!item.isStackable()) {
                slots += i.getCount();
            } else if (player.getInventory().getItemByItemId(item.getId()) == null) {
                slots++;
            }
        }

        // Item Max Limit Check
        if (!player.getInventory().validateCapacity(slots)) {
            player.sendPacket(SystemMessageId.YOUR_INVENTORY_IS_FULL);
            return;
        }

        // Weight limit Check
        if (!player.getInventory().validateWeight(weight)) {
            player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
            return;
        }

        // Proceed to the transfer
        final InventoryUpdate playerIU = new InventoryUpdate();
        for (var holder : _items) {
            if (!transferItem(player, warehouse, manager, playerIU, holder)) {
                return;
            }
        }
        player.sendInventoryUpdate(playerIU);
    }

    private boolean transferItem(Player player, ItemContainer warehouse, Npc manager, InventoryUpdate playerIU, ItemHolder holder) {
        final Item oldItem = warehouse.getItemByObjectId(holder.getId());
        if (oldItem == null || oldItem.getCount() < holder.getCount()) {
            LOGGER.warn("Error withdrawing a warehouse object for {} (olditem == null)", player);
            return false;
        }
        final Item newItem = warehouse.transferItem(warehouse.getName(), holder.getId(), holder.getCount(), player.getInventory(), player, manager);
        if (newItem == null) {
            LOGGER.warn("Error withdrawing a warehouse object for {}  (newitem == null)", player);
            return false;
        }

        if (newItem.getCount() > holder.getCount()) {
            playerIU.addModifiedItem(newItem);
        } else {
            playerIU.addNewItem(newItem);
        }
        return true;
    }

    private boolean canPlayerWithdraw(Player player, ItemContainer warehouse, Npc manager) {
        if (warehouse == null) {
            return false;
        }

        if ((manager == null || !manager.isWarehouse() || !manager.canInteract(player)) && !player.isGM()) {
            return false;
        }

        if (!(warehouse instanceof PlayerWarehouse) && !player.getAccessLevel().allowTransaction()) {
            player.sendMessage("Transactions are disabled for your Access Level.");
            return false;
        }

        if (player.getReputation() < 0 && !CharacterSettings.canPkUseWareHouse()) {
            return false;
        }

        if (ClanSettings.canMembersWithdrawFromWarehouse()) {
            return (!(warehouse instanceof ClanWarehouse)) || player.hasClanPrivilege(ClanPrivilege.CL_VIEW_WAREHOUSE);
        } else if ((warehouse instanceof ClanWarehouse) && !player.isClanLeader()) {
            player.sendPacket(SystemMessageId.ITEMS_LEFT_AT_THE_CLAN_HALL_WAREHOUSE_CAN_ONLY_BE_RETRIEVED_BY_THE_CLAN_LEADER_DO_YOU_WANT_TO_CONTINUE);
            return false;
        }
        return true;
    }
}
