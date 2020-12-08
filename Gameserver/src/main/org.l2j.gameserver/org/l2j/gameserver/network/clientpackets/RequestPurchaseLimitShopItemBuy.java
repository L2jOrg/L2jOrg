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

import org.l2j.gameserver.data.database.dao.LCoinShopDAO;
import org.l2j.gameserver.engine.item.shop.LCoinShop;
import org.l2j.gameserver.engine.item.shop.lcoin.LCoinShopProduct;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.LCoinShopRequest;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.network.serverpackets.l2coin.ExPurchaseLimitShopItemBuy;

import java.util.List;

import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.gameserver.network.SystemMessageId.NOT_ENOUGH_L2_COINS_TO_BUY_IT;
import static org.l2j.gameserver.network.SystemMessageId.UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY;

/**
 * @author JoeAlisson
 */
public class RequestPurchaseLimitShopItemBuy extends ClientPacket {
    private int productId;
    private int amount;
    private byte tab;

    @Override
    protected void readImpl() throws Exception {
        tab = readByte();
        productId = readInt();
        amount = readInt();
    }

    @Override
    protected void runImpl() {
        final Player player = client.getPlayer();

        LCoinShopProduct product = LCoinShop.getInstance().getProductInfo(productId);

        if (!validate(player, product)) {
            player.sendPacket(ExPurchaseLimitShopItemBuy.fail(product, tab));
            return;
        }

        player.addRequest(new LCoinShopRequest(player));

        List<ItemHolder> ingredients = product.ingredients();
        consumeIngredients(player, ingredients);

        ItemHolder productItem = product.production();
        player.addItem("LCoinShop", productItem.getId(), productItem.getCount() * amount, player, true);

        player.sendPacket(ExPurchaseLimitShopItemBuy.success(product, tab));
        if(product.restrictionAmount() > 0) {
            LCoinShop.getInstance().addHistory(player, product, amount);
            getDAO(LCoinShopDAO.class).saveHistory(player.getAccountName(), product.id(), amount, product.restrictionPeriod());
        }
        player.removeRequest(LCoinShopRequest.class);
    }

    private boolean validate(Player player, LCoinShopProduct product) {
        if(player.hasItemRequest() || player.hasRequest(LCoinShopRequest.class) ){
            return false;
        }

        if(player.getLevel() < product.minLevel() || product.isExpired()) {
            return false;
        }

        if(product.restrictionAmount() > 0 && product.restrictionAmount() < LCoinShop.getInstance().boughtCount(player, product) + amount) {
            return false;
        }

        if(!player.isInventoryUnder80() || player.getWeightPenalty() >= 3) {
            player.sendPacket(UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
            return false;
        }

        return hasIngredients(player, product.ingredients());
    }

    private boolean hasIngredients(Player player, List<ItemHolder> ingredients) {
        for (ItemHolder ingredient : ingredients)
            if (player.getInventory().getInventoryItemCount(ingredient.getId(), -1) < Math.multiplyExact(ingredient.getCount(), amount)) {
                if(ingredient.getId() == CommonItem.L2_COIN) {
                    player.sendPacket(NOT_ENOUGH_L2_COINS_TO_BUY_IT);
                }
                return false;
            }
        return true;
    }

    private void consumeIngredients(Player player, List<ItemHolder> ingredients) {
        for (ItemHolder ingredient : ingredients) {
            player.destroyItemByItemId("LCoinShop", ingredient.getId(), ingredient.getCount() * amount, player, true);
        }
    }
}
