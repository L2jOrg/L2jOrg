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

import org.l2j.gameserver.data.xml.impl.BuyListData;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.enums.TaxType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Merchant;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.buylist.ProductList;
import org.l2j.gameserver.model.holders.UniqueItemHolder;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ExBuySellList;
import org.l2j.gameserver.network.serverpackets.ExUserInfoInvenWeight;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

import static org.l2j.gameserver.model.actor.Npc.INTERACTION_DISTANCE;
import static org.l2j.gameserver.util.MathUtil.isInsideRadius3D;


/**
 * RequestSellItem client packet class.
 */
public final class RequestSellItem extends ClientPacket {
    private static final int BATCH_LENGTH = 16;
    private static final int CUSTOM_CB_SELL_LIST = 423;

    private int _listId;
    private List<UniqueItemHolder> _items = null;

    @Override
    public void readImpl() throws InvalidDataPacketException {
        _listId = readInt();
        final int size = readInt();
        if (size <= 0 || size > CharacterSettings.maxItemInPacket() || size * BATCH_LENGTH != available()) {
            throw new InvalidDataPacketException("Invalid Size " + size);
        }

        _items = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            final int objectId = readInt();
            final int itemId = readInt();
            final long count = readLong();
            if ((objectId < 1) || (itemId < 1) || (count < 1)) {
                _items = null;
                throw new InvalidDataPacketException("Invalid object id " + objectId + " or item id " + itemId + "or count " + count);
            }
            _items.add(new UniqueItemHolder(itemId, objectId, count));
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

        if (player.getReputation() < 0 && !CharacterSettings.canPkShop()) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final WorldObject target = player.getTarget();
        Merchant merchant = null;
        if (!player.isGM() && (_listId != CUSTOM_CB_SELL_LIST)) {
            if ((target == null) || !isInsideRadius3D(player, target, INTERACTION_DISTANCE) || (player.getInstanceId() != target.getInstanceId())) {
                client.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
            if (target instanceof Merchant) {
                merchant = (Merchant) target;
            } else {
                client.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
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

        long totalPrice = 0;
        // Proceed the sell
        for (UniqueItemHolder i : _items) {
            Item item = player.checkItemManipulation(i.getObjectId(), i.getCount(), "sell");
            if ((item == null) || (!item.isSellable())) {
                continue;
            }

            long price = item.getReferencePrice() / 2;
            totalPrice += price * i.getCount();
            if (MathUtil.checkMulOverFlow(price, i.getCount(), CharacterSettings.maxAdena())) {
                GameUtils.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase over " + CharacterSettings.maxAdena() + " adena worth of goods.");
                return;
            }

            if (GeneralSettings.allowRefund()) {
                player.getInventory().transferItem("Sell", i.getObjectId(), i.getCount(), player.getRefund(), player, merchant);
            } else {
                player.getInventory().destroyItem("Sell", i.getObjectId(), i.getCount(), player, merchant);
            }
        }

        // add to castle treasury
        if (merchant != null) {
            // Keep here same formula as in {@link ExBuySellList} to produce same result.
            final long profit = (long) (totalPrice * (1.0 - merchant.getCastleTaxRate(TaxType.SELL)));
            merchant.handleTaxPayment(totalPrice - profit);
            totalPrice = profit;
        }

        player.addAdena("Sell", totalPrice, merchant, false);

        // Update current load as well
        client.sendPacket(new ExUserInfoInvenWeight());
        client.sendPacket(new ExBuySellList(player, true));
    }
}
