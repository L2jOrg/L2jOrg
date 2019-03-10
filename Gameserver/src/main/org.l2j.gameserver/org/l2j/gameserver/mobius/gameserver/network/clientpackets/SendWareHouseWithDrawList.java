package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.mobius.gameserver.model.itemcontainer.ClanWarehouse;
import org.l2j.gameserver.mobius.gameserver.model.itemcontainer.ItemContainer;
import org.l2j.gameserver.mobius.gameserver.model.itemcontainer.PcWarehouse;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.mobius.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * This class ... 32 SendWareHouseWithDrawList cd (dd) WootenGil rox :P
 *
 * @version $Revision: 1.2.2.1.2.4 $ $Date: 2005/03/29 23:15:16 $
 */
public final class SendWareHouseWithDrawList extends IClientIncomingPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendWareHouseWithDrawList.class);
    private static final int BATCH_LENGTH = 12; // length of the one item

    private ItemHolder _items[] = null;

    @Override
    public void readImpl(ByteBuffer packet) throws InvalidDataPacketException {
        final int count = packet.getInt();
        if ((count <= 0) || (count > Config.MAX_ITEM_IN_PACKET) || ((count * BATCH_LENGTH) != packet.remaining())) {
            throw new InvalidDataPacketException();
        }

        _items = new ItemHolder[count];
        for (int i = 0; i < count; i++) {
            final int objId = packet.getInt();
            final long cnt = packet.getLong();
            if ((objId < 1) || (cnt < 0)) {
                _items = null;
                throw new InvalidDataPacketException();
            }
            _items[i] = new ItemHolder(objId, cnt);
        }
    }

    @Override
    public void runImpl() {
        if (_items == null) {
            return;
        }

        final L2PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }

        if (!client.getFloodProtectors().getTransaction().tryPerformAction("withdraw")) {
            player.sendMessage("You are withdrawing items too fast.");
            return;
        }

        final ItemContainer warehouse = player.getActiveWarehouse();
        if (warehouse == null) {
            return;
        }

        final L2Npc manager = player.getLastFolkNPC();
        if (((manager == null) || !manager.isWarehouse() || !manager.canInteract(player)) && !player.isGM()) {
            return;
        }

        if (!(warehouse instanceof PcWarehouse) && !player.getAccessLevel().allowTransaction()) {
            player.sendMessage("Transactions are disabled for your Access Level.");
            return;
        }

        // Alt game - Karma punishment
        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE && (player.getReputation() < 0)) {
            return;
        }

        if (Config.ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH) {
            if ((warehouse instanceof ClanWarehouse) && !player.hasClanPrivilege(ClanPrivilege.CL_VIEW_WAREHOUSE)) {
                return;
            }
        } else if ((warehouse instanceof ClanWarehouse) && !player.isClanLeader()) {
            player.sendPacket(SystemMessageId.ITEMS_LEFT_AT_THE_CLAN_HALL_WAREHOUSE_CAN_ONLY_BE_RETRIEVED_BY_THE_CLAN_LEADER_DO_YOU_WANT_TO_CONTINUE);
            return;
        }

        int weight = 0;
        int slots = 0;

        for (ItemHolder i : _items) {
            // Calculate needed slots
            final L2ItemInstance item = warehouse.getItemByObjectId(i.getId());
            if ((item == null) || (item.getCount() < i.getCount())) {
                Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to withdraw non-existent item from warehouse.", Config.DEFAULT_PUNISH);
                return;
            }

            weight += i.getCount() * item.getItem().getWeight();
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
        final InventoryUpdate playerIU = Config.FORCE_INVENTORY_UPDATE ? null : new InventoryUpdate();
        for (ItemHolder i : _items) {
            final L2ItemInstance oldItem = warehouse.getItemByObjectId(i.getId());
            if ((oldItem == null) || (oldItem.getCount() < i.getCount())) {
                LOGGER.warn("Error withdrawing a warehouse object for char " + player.getName() + " (olditem == null)");
                return;
            }
            final L2ItemInstance newItem = warehouse.transferItem(warehouse.getName(), i.getId(), i.getCount(), player.getInventory(), player, manager);
            if (newItem == null) {
                LOGGER.warn("Error withdrawing a warehouse object for char " + player.getName() + " (newitem == null)");
                return;
            }

            if (playerIU != null) {
                if (newItem.getCount() > i.getCount()) {
                    playerIU.addModifiedItem(newItem);
                } else {
                    playerIU.addNewItem(newItem);
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
