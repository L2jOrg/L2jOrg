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
package org.l2j.gameserver.network.serverpackets.vip;

import org.l2j.gameserver.data.xml.impl.PrimeShopData;
import org.l2j.gameserver.model.primeshop.PrimeShopItem;
import org.l2j.gameserver.model.primeshop.PrimeShopProduct;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import static java.util.Objects.nonNull;

public class ReceiveVipProductList extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client) {
        var player = client.getPlayer();
        var products = PrimeShopData.getInstance().getPrimeItems();
        var gift = PrimeShopData.getInstance().getVipGiftOfTier(player.getVipTier());

        writeId(ServerExPacketId.EX_BR_VIP_PRODUCT_LIST_ACK);
        writeLong(player.getAdena());
        writeLong(player.getRustyCoin()); // Rusty Coin Amount
        writeLong(player.getSilverCoin()); // Silver Coin Amount
        writeByte(1); // Show Reward tab

        if(nonNull(gift)) {
            writeInt(products.size() + 1);
            putProduct(gift);
        } else {
            writeInt(products.size());
        }

        for (var product : products.values()) {
            putProduct(product);
        }
    }

    private void putProduct(PrimeShopProduct product) {
        writeInt(product.getId());
        writeByte(product.getCategory());
        writeByte(product.getPaymentType());
        writeInt(product.getPrice()); // L2 Coin | Rusty Coin seems to use the same field based on payment type
        writeInt(product.getSilverCoin());
        writeByte(product.getPanelType()); // NEW - 6; HOT - 5 ... Unk
        writeByte(product.getVipTier());
        writeByte(10); // Unk

        writeByte(product.getItems().size());

        for (PrimeShopItem item : product.getItems()) {
            writeInt(item.getId());
            writeInt((int) item.getCount());
        }
    }


}
