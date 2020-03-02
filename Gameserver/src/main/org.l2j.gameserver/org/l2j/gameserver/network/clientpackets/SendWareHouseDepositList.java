package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.itemcontainer.ItemContainer;
import org.l2j.gameserver.model.itemcontainer.PcWarehouse;
import org.l2j.gameserver.model.items.CommonItem;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * SendWareHouseDepositList client packet class.
 */
public final class SendWareHouseDepositList extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendWareHouseDepositList.class);
    private static final int BATCH_LENGTH = 12;

    private List<ItemHolder> _items = null;

    @Override
    public void readImpl() throws InvalidDataPacketException {
        final int size = readInt();
        if ((size <= 0) || (size > Config.MAX_ITEM_IN_PACKET) || ((size * BATCH_LENGTH) != available())) {
            throw new InvalidDataPacketException();
        }

        _items = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            final int objId = readInt();
            final long count = readLong();
            if ((objId < 1) || (count < 0)) {
                _items = null;
                throw new InvalidDataPacketException();
            }
            _items.add(new ItemHolder(objId, count));
        }
    }

    @Override
    public void runImpl() {
        if (_items == null) {
            return;
        }

        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        if (!client.getFloodProtectors().getTransaction().tryPerformAction("deposit")) {
            player.sendMessage("You are depositing items too fast.");
            return;
        }

        final ItemContainer warehouse = player.getActiveWarehouse();
        if (warehouse == null) {
            return;
        }
        final boolean isPrivate = warehouse instanceof PcWarehouse;

        final Npc manager = player.getLastFolkNPC();
        if (((manager == null) || !manager.isWarehouse() || !manager.canInteract(player)) && !player.isGM()) {
            return;
        }

        if (!isPrivate && !player.getAccessLevel().allowTransaction()) {
            player.sendMessage("Transactions are disabled for your Access Level.");
            return;
        }

        if (player.hasItemRequest()) {
            GameUtils.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to use enchant Exploit!");
            return;
        }

        // Alt game - Karma punishment
        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE && (player.getReputation() < 0)) {
            return;
        }

        // Freight price from config or normal price per item slot (30)
        final long fee = _items.size() * 30;
        long currentAdena = player.getAdena();
        int slots = 0;

        for (ItemHolder i : _items) {
            final Item item = player.checkItemManipulation(i.getId(), i.getCount(), "deposit");
            if (item == null) {
                LOGGER.warn("Error depositing a warehouse object for char " + player.getName() + " (validity check)");
                return;
            }

            // Calculate needed adena and slots
            if (item.getId() == CommonItem.ADENA) {
                currentAdena -= i.getCount();
            }
            if (!item.isStackable()) {
                slots += i.getCount();
            } else if (warehouse.getItemByItemId(item.getId()) == null) {
                slots++;
            }
        }

        // Item Max Limit Check
        if (!warehouse.validateCapacity(slots)) {
            client.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        }

        // Check if enough adena and charge the fee
        if ((currentAdena < fee) || !player.reduceAdena(warehouse.getName(), fee, manager, false)) {
            client.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
            return;
        }

        // get current tradelist if any
        if (player.getActiveTradeList() != null) {
            return;
        }

        // Proceed to the transfer
        final InventoryUpdate playerIU = Config.FORCE_INVENTORY_UPDATE ? null : new InventoryUpdate();
        for (ItemHolder i : _items) {
            // Check validity of requested item
            final Item oldItem = player.checkItemManipulation(i.getId(), i.getCount(), "deposit");
            if (oldItem == null) {
                LOGGER.warn("Error depositing a warehouse object for char " + player.getName() + " (olditem == null)");
                return;
            }

            if (!oldItem.isDepositable(isPrivate) || !oldItem.isAvailable(player, true, isPrivate)) {
                continue;
            }

            final Item newItem = player.getInventory().transferItem(warehouse.getName(), i.getId(), i.getCount(), warehouse, player, manager);
            if (newItem == null) {
                LOGGER.warn("Error depositing a warehouse object for char " + player.getName() + " (newitem == null)");
                continue;
            }

            if (playerIU != null) {
                if ((oldItem.getCount() > 0) && (oldItem != newItem)) {
                    playerIU.addModifiedItem(oldItem);
                } else {
                    playerIU.addRemovedItem(oldItem);
                }
            }
        }

        // Send updated item list to the player
        if (playerIU != null) {
            player.sendInventoryUpdate(playerIU);
        } else {
            player.sendItemList();
        }
    }
}
