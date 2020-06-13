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

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.BuyListData;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Merchant;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.buylist.Product;
import org.l2j.gameserver.model.buylist.ProductList;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ExUserInfoEquipSlot;
import org.l2j.gameserver.network.serverpackets.ShopPreviewInfo;
import org.l2j.gameserver.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.util.MathUtil.isInsideRadius2D;

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
        final Player activeChar = client.getPlayer();
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
        final WorldObject target = activeChar.getTarget();
        // No target (i.e. GM Shop)
        if (!activeChar.isGM() && (!((target instanceof Merchant)) // Target not a merchant
                || !isInsideRadius2D(activeChar, target, Npc.INTERACTION_DISTANCE) // Distance is too far
        )) {
            return;
        }

        if ((_count < 1) || (_listId >= 4000000)) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Get the current merchant targeted by the player
        final Merchant merchant = (target instanceof Merchant) ? (Merchant) target : null;
        if (merchant == null) {
            LOGGER.warn("Null merchant!");
            return;
        }

        final ProductList buyList = BuyListData.getInstance().getBuyList(_listId);
        if (buyList == null) {
            GameUtils.handleIllegalPlayerAction(activeChar, "Warning!! Character " + activeChar.getName() + " of account " + activeChar.getAccountName() + " sent a false BuyList list_id " + _listId);
            return;
        }

        long totalPrice = 0;
        final EnumMap<InventorySlot, Integer> items = new EnumMap<>(InventorySlot.class);

        for (int i = 0; i < _count; i++) {
            final int itemId = _items[i];

            final Product product = buyList.getProductByItemId(itemId);
            if (product == null) {
                GameUtils.handleIllegalPlayerAction(activeChar, "Warning!! Character " + activeChar.getName() + " of account " + activeChar.getAccountName() + " sent a false BuyList list_id " + _listId + " and item_id " + itemId);
                return;
            }

            var slot = product.getBodyPart().slot();
            if (isNull(slot)) {
                continue;
            }

            if (items.containsKey(slot)) {
                activeChar.sendPacket(SystemMessageId.YOU_CAN_NOT_TRY_THOSE_ITEMS_ON_AT_THE_SAME_TIME);
                return;
            }

            items.put(slot, itemId);
            totalPrice += Config.WEAR_PRICE;
            if (totalPrice > Inventory.MAX_ADENA) {
                GameUtils.handleIllegalPlayerAction(activeChar, "Warning!! Character " + activeChar.getName() + " of account " + activeChar.getAccountName() + " tried to purchase over " + Inventory.MAX_ADENA + " adena worth of goods.");
                return;
            }
        }

        // Charge buyer and add tax to castle treasury if not owned by npc clan because a Try On is not Free
        if ((totalPrice < 0) || !activeChar.reduceAdena("Wear", totalPrice, activeChar.getLastFolkNPC(), true)) {
            activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
            return;
        }

        if (!items.isEmpty()) {
            activeChar.sendPacket(new ShopPreviewInfo(items));
            // Schedule task
            ThreadPool.schedule(new RemoveWearItemsTask(activeChar), Config.WEAR_DELAY * 1000);
        }
    }

    private class RemoveWearItemsTask implements Runnable {
        private final Player activeChar;

        protected RemoveWearItemsTask(Player player) {
            activeChar = player;
        }

        @Override
        public void run() {
            try {
                activeChar.sendPacket(SystemMessageId.YOU_ARE_NO_LONGER_TRYING_ON_EQUIPMENT);
                activeChar.sendPacket(new ExUserInfoEquipSlot(activeChar));
            } catch (Exception e) {
                LOGGER.error(e.getLocalizedMessage(), e);
            }
        }
    }

}
