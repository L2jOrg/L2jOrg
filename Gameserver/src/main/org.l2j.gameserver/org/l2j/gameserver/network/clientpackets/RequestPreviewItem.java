package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.gameserver.data.xml.impl.BuyListData;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2MerchantInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.buylist.Product;
import org.l2j.gameserver.model.buylist.ProductList;
import org.l2j.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.model.items.L2Item;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ExUserInfoEquipSlot;
import org.l2j.gameserver.network.serverpackets.ShopPreviewInfo;
import org.l2j.gameserver.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * * @author Gnacik
 */
public final class RequestPreviewItem extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestPreviewItem.class);
    @SuppressWarnings("unused")
    private int _unk;
    private int _listId;
    private int _count;
    private int[] _items;

    @Override
    public void readImpl() throws InvalidDataPacketException {
        _unk = readInt();
        _listId = readInt();
        _count = readInt();

        if (_count < 0) {
            _count = 0;
        }
        if (_count > 100) {
            throw new InvalidDataPacketException(); // prevent too long lists
        }

        // Create _items table that will contain all ItemID to Wear
        _items = new int[_count];

        // Fill _items table with all ItemID to Wear
        for (int i = 0; i < _count; i++) {
            _items[i] = readInt();
        }
    }

    @Override
    public void runImpl() {
        if (_items == null) {
            return;
        }

        // Get the current player and return if null
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (!client.getFloodProtectors().getTransaction().tryPerformAction("buy")) {
            activeChar.sendMessage("You are buying too fast.");
            return;
        }

        // If Alternate rule Karma punishment is set to true, forbid Wear to player with Karma
        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && (activeChar.getReputation() < 0)) {
            return;
        }

        // Check current target of the player and the INTERACTION_DISTANCE
        final L2Object target = activeChar.getTarget();
        if (!activeChar.isGM() && ((target == null // No target (i.e. GM Shop)
        ) || !((target instanceof L2MerchantInstance)) // Target not a merchant
                || !activeChar.isInsideRadius2D(target, L2Npc.INTERACTION_DISTANCE) // Distance is too far
        )) {
            return;
        }

        if ((_count < 1) || (_listId >= 4000000)) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Get the current merchant targeted by the player
        final L2MerchantInstance merchant = (target instanceof L2MerchantInstance) ? (L2MerchantInstance) target : null;
        if (merchant == null) {
            LOGGER.warn("Null merchant!");
            return;
        }

        final ProductList buyList = BuyListData.getInstance().getBuyList(_listId);
        if (buyList == null) {
            GameUtils.handleIllegalPlayerAction(activeChar, "Warning!! Character " + activeChar.getName() + " of account " + activeChar.getAccountName() + " sent a false BuyList list_id " + _listId, Config.DEFAULT_PUNISH);
            return;
        }

        long totalPrice = 0;
        final Map<Integer, Integer> itemList = new HashMap<>();

        for (int i = 0; i < _count; i++) {
            final int itemId = _items[i];

            final Product product = buyList.getProductByItemId(itemId);
            if (product == null) {
                GameUtils.handleIllegalPlayerAction(activeChar, "Warning!! Character " + activeChar.getName() + " of account " + activeChar.getAccountName() + " sent a false BuyList list_id " + _listId + " and item_id " + itemId, Config.DEFAULT_PUNISH);
                return;
            }

            final L2Item template = product.getItem();
            if (template == null) {
                continue;
            }

            final int slot = Inventory.getPaperdollIndex(template.getBodyPart());
            if (slot < 0) {
                continue;
            }

            if (itemList.containsKey(slot)) {
                activeChar.sendPacket(SystemMessageId.YOU_CAN_NOT_TRY_THOSE_ITEMS_ON_AT_THE_SAME_TIME);
                return;
            }

            itemList.put(slot, itemId);
            totalPrice += Config.WEAR_PRICE;
            if (totalPrice > Inventory.MAX_ADENA) {
                GameUtils.handleIllegalPlayerAction(activeChar, "Warning!! Character " + activeChar.getName() + " of account " + activeChar.getAccountName() + " tried to purchase over " + Inventory.MAX_ADENA + " adena worth of goods.", Config.DEFAULT_PUNISH);
                return;
            }
        }

        // Charge buyer and add tax to castle treasury if not owned by npc clan because a Try On is not Free
        if ((totalPrice < 0) || !activeChar.reduceAdena("Wear", totalPrice, activeChar.getLastFolkNPC(), true)) {
            activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            return;
        }

        if (!itemList.isEmpty()) {
            activeChar.sendPacket(new ShopPreviewInfo(itemList));
            // Schedule task
            ThreadPoolManager.getInstance().schedule(new RemoveWearItemsTask(activeChar), Config.WEAR_DELAY * 1000);
        }
    }

    private class RemoveWearItemsTask implements Runnable {
        private final L2PcInstance activeChar;

        protected RemoveWearItemsTask(L2PcInstance player) {
            activeChar = player;
        }

        @Override
        public void run() {
            try {
                activeChar.sendPacket(SystemMessageId.YOU_ARE_NO_LONGER_TRYING_ON_EQUIPMENT_2);
                activeChar.sendPacket(new ExUserInfoEquipSlot(activeChar));
            } catch (Exception e) {
                LOGGER.error(e.getLocalizedMessage(), e);
            }
        }
    }

}
