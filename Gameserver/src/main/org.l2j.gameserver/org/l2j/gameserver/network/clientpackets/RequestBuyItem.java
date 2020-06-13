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

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.BuyListData;
import org.l2j.gameserver.enums.TaxType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Merchant;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.buylist.Product;
import org.l2j.gameserver.model.buylist.ProductList;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ExBuySellList;
import org.l2j.gameserver.network.serverpackets.ExUserInfoInvenWeight;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.l2j.gameserver.model.actor.Npc.INTERACTION_DISTANCE;
import static org.l2j.gameserver.util.MathUtil.isInsideRadius3D;

public final class RequestBuyItem extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestBuyItem.class);
    private static final int BATCH_LENGTH = 12;
    private static final int CUSTOM_CB_SELL_LIST = 423;

    private int _listId;
    private List<ItemHolder> _items = null;

    @Override
    public void readImpl() throws InvalidDataPacketException {
        _listId = readInt();
        final int size = readInt();
        if ((size <= 0) || (size > Config.MAX_ITEM_IN_PACKET) || ((size * BATCH_LENGTH) != available())) {
            throw new InvalidDataPacketException();
        }

        _items = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            final int itemId = readInt();
            final long count = readLong();
            if ((itemId < 1) || (count < 1)) {
                _items = null;
                throw new InvalidDataPacketException();
            }
            _items.add(new ItemHolder(itemId, count));
        }
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        if (!client.getFloodProtectors().getTransaction().tryPerformAction("buy")) {
            player.sendMessage("You are buying too fast.");
            return;
        }

        if (_items == null) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Alt game - Karma punishment
        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && (player.getReputation() < 0)) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final WorldObject target = player.getTarget();
        Merchant merchant = null;
        if (!player.isGM() && (_listId != CUSTOM_CB_SELL_LIST)) {
            if (!(target instanceof Merchant) || (!isInsideRadius3D(player, target, INTERACTION_DISTANCE)) || (player.getInstanceWorld() != target.getInstanceWorld())) {
                client.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
            merchant = (Merchant) target; // FIXME: Doesn't work for GMs.
        }

        if ((merchant == null) && !player.isGM() && (_listId != CUSTOM_CB_SELL_LIST)) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final ProductList buyList = BuyListData.getInstance().getBuyList(_listId);
        if (buyList == null) {
            GameUtils.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false BuyList list_id " + _listId);
            return;
        }

        double castleTaxRate = 0;
        if (merchant != null) {
            if (!buyList.isNpcAllowed(merchant.getId())) {
                client.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
            castleTaxRate = merchant.getCastleTaxRate(TaxType.BUY);
        }

        long subTotal = 0;

        // Check for buylist validity and calculates summary values
        long slots = 0;
        long weight = 0;
        for (ItemHolder i : _items) {
            final Product product = buyList.getProductByItemId(i.getId());
            if (product == null) {
                GameUtils.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false BuyList list_id " + _listId + " and item_id " + i.getId());
                return;
            }

            if (!product.isStackable() && (i.getCount() > 1)) {
                GameUtils.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase invalid quantity of items at the same time.");
                client.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED));
                return;
            }

            long price = product.getPrice();
            if (price < 0) {
                LOGGER.warn("ERROR, no price found .. wrong buylist ??");
                client.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }

            if ((price == 0) && !player.isGM() && Config.ONLY_GM_ITEMS_FREE) {
                player.sendMessage("Ohh Cheat dont work? You have a problem now!");
                GameUtils.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried buy item for 0 adena.");
                return;
            }

            if (product.hasLimitedStock()) {
                // trying to buy more then available
                if (i.getCount() > product.getCount()) {
                    client.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
            }

            if ((Inventory.MAX_ADENA / i.getCount()) < price) {
                GameUtils.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase over " + Inventory.MAX_ADENA + " adena worth of goods.");
                return;
            }
            // first calculate price per item with tax, then multiply by count
            price = (long) (price * (1 + castleTaxRate + product.getBaseTaxRate()));
            subTotal += i.getCount() * price;
            if (subTotal > Inventory.MAX_ADENA) {
                GameUtils.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase over " + Inventory.MAX_ADENA + " adena worth of goods.");
                return;
            }

            weight += i.getCount() * product.getWeight();
            if (player.getInventory().getItemByItemId(product.getItemId()) == null) {
                slots++;
            }
        }

        if (!player.isGM() && ((weight > Integer.MAX_VALUE) || (weight < 0) || !player.getInventory().validateWeight((int) weight))) {
            client.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (!player.isGM() && ((slots > Integer.MAX_VALUE) || (slots < 0) || !player.getInventory().validateCapacity((int) slots))) {
            client.sendPacket(SystemMessageId.YOUR_INVENTORY_IS_FULL);
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Charge buyer and add tax to castle treasury if not owned by npc clan
        if ((subTotal < 0) || !player.reduceAdena("Buy", subTotal, player.getLastFolkNPC(), false)) {
            client.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Proceed the purchase
        for (ItemHolder i : _items) {
            final Product product = buyList.getProductByItemId(i.getId());
            if (product == null) {
                GameUtils.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false BuyList list_id " + _listId + " and item_id " + i.getId());
                continue;
            }

            if (product.hasLimitedStock()) {
                if (product.decreaseCount(i.getCount())) {
                    player.getInventory().addItem("Buy", i.getId(), i.getCount(), player, merchant);
                }
            } else {
                player.getInventory().addItem("Buy", i.getId(), i.getCount(), player, merchant);
            }
        }

        // add to castle treasury
        if (merchant != null) {
            merchant.handleTaxPayment((long) (subTotal * castleTaxRate));
        }

        client.sendPacket(new ExUserInfoInvenWeight(player));
        client.sendPacket(new ExBuySellList(player, true));
        player.sendPacket(SystemMessageId.EXCHANGE_IS_SUCCESSFUL);
    }
}
