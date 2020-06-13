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

import org.l2j.gameserver.data.xml.impl.LCoinShopData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.LCoinShopRequest;
import org.l2j.gameserver.network.serverpackets.ExPurchaseLimitShopItemBuy;

public class RequestPurchaseLimitShopItemBuy extends ClientPacket {
    private int productId;
    private int amount;
    @Override
    protected void readImpl() throws Exception {
        readByte();
        productId = readInt();
        amount = readInt();
    }

    @Override
    protected void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        var product = LCoinShopData.getInstance().getProductInfo(productId);
        var productItem = product.getProduction();

        if (player.hasItemRequest() || player.hasRequest(LCoinShopRequest.class) || player.getLCoins() < product.getIngredients().get(0).getCount()) {
            player.sendPacket(new ExPurchaseLimitShopItemBuy(LCoinShopData.getInstance().getProductInfo(productId), true));
            return;
        }

        player.addRequest(new LCoinShopRequest(player));
        player.addLCoins(-product.getIngredients().get(0).getCount());
        player.addItem("LCoinShop", productItem.getId(), productItem.getCount() * amount, player, true);
        player.removeRequest(LCoinShopRequest.class);
    }
}
