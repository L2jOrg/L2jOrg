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
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Merchant;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.buylist.ProductList;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ExBuySellList;
import org.l2j.gameserver.network.serverpackets.ExUserInfoInvenWeight;
import org.l2j.gameserver.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.l2j.gameserver.model.actor.Npc.INTERACTION_DISTANCE;
import static org.l2j.gameserver.util.MathUtil.isInsideRadius3D;


/**
 * RequestRefundItem client packet class.
 */
public final class RequestRefundItem extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestRefundItem.class);
    private static final int BATCH_LENGTH = 4; // length of the one item
    private static final int CUSTOM_CB_SELL_LIST = 423;

    private int _listId;
    private int[] _items = null;

    @Override
    public void readImpl() throws InvalidDataPacketException {
        _listId = readInt();
        final int count = readInt();
        if ((count <= 0) || (count > Config.MAX_ITEM_IN_PACKET) || ((count * BATCH_LENGTH) != available())) {
            throw new InvalidDataPacketException();
        }

        _items = new int[count];
        for (int i = 0; i < count; i++) {
            _items[i] = readInt();
        }
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        if (!client.getFloodProtectors().getTransaction().tryPerformAction("refund")) {
            player.sendMessage("You are using refund too fast.");
            return;
        }

        if ((_items == null) || !player.hasRefund()) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final WorldObject target = player.getTarget();
        Merchant merchant = null;
        if (!player.isGM() && (_listId != CUSTOM_CB_SELL_LIST)) {
            if (!(target instanceof Merchant) || !isInsideRadius3D(player, target, INTERACTION_DISTANCE) || (player.getInstanceId() != target.getInstanceId())) {
                client.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
            merchant = (Merchant) target;
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

        if ((merchant != null) && !buyList.isNpcAllowed(merchant.getId())) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        long weight = 0;
        long adena = 0;
        long slots = 0;

        final Item[] refund = player.getRefund().getItems().toArray(new Item[0]);
        final int[] objectIds = new int[_items.length];

        for (int i = 0; i < _items.length; i++) {
            final int idx = _items[i];
            if ((idx < 0) || (idx >= refund.length)) {
                GameUtils.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent invalid refund index");
                return;
            }

            // check for duplicates - indexes
            for (int j = i + 1; j < _items.length; j++) {
                if (idx == _items[j]) {
                    GameUtils.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent duplicate refund index");
                    return;
                }
            }

            final Item item = refund[idx];
            final ItemTemplate template = item.getTemplate();
            objectIds[i] = item.getObjectId();

            // second check for duplicates - object ids
            for (int j = 0; j < i; j++) {
                if (objectIds[i] == objectIds[j]) {
                    GameUtils.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " has duplicate items in refund list");
                    return;
                }
            }

            final long count = item.getCount();
            weight += count * template.getWeight();
            adena += (count * template.getReferencePrice()) / 2;
            if (!template.isStackable()) {
                slots += count;
            } else if (player.getInventory().getItemByItemId(template.getId()) == null) {
                slots++;
            }
        }

        if ((weight > Integer.MAX_VALUE) || (weight < 0) || !player.getInventory().validateWeight((int) weight)) {
            client.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if ((slots > Integer.MAX_VALUE) || (slots < 0) || !player.getInventory().validateCapacity((int) slots)) {
            client.sendPacket(SystemMessageId.YOUR_INVENTORY_IS_FULL);
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if ((adena < 0) || !player.reduceAdena("Refund", adena, player.getLastFolkNPC(), false)) {
            client.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        for (int i = 0; i < _items.length; i++) {
            final Item item = player.getRefund().transferItem("Refund", objectIds[i], Long.MAX_VALUE, player.getInventory(), player, player.getLastFolkNPC());
            if (item == null) {
                LOGGER.warn("Error refunding object for char " + player.getName() + " (newitem == null)");
                continue;
            }
        }

        // Update current load status on player
        client.sendPacket(new ExUserInfoInvenWeight(player));
        client.sendPacket(new ExBuySellList(player, true));
    }
}
