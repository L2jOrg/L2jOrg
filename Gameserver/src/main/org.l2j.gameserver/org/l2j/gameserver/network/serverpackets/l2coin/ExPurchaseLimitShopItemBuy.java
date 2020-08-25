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
package org.l2j.gameserver.network.serverpackets.l2coin;

import org.l2j.gameserver.engine.item.shop.lcoin.LCoinShopProduct;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExPurchaseLimitShopItemBuy extends ServerPacket {

    private final boolean fail;
    private final LCoinShopProduct product;
    private final byte tab;

    private ExPurchaseLimitShopItemBuy(LCoinShopProduct product, byte tab, boolean fail) {
        this.fail = fail;
        this.tab = tab;
        this.product = product;
    }

    @Override
    protected void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_PURCHASE_LIMIT_SHOP_ITEM_BUY);
        writeByte(fail);
        writeByte(tab);
        writeInt(product.id());

        ItemHolder production = product.production();
        int size = 1;
        writeInt(size); // size
        for (int i = 0; i < size ; i++) {
            writeByte(i);
            writeInt(production.getId()); //item id
            writeInt((int) production.getCount()); // count
        }

        writeInt(product.getRemainAmount()); // remain item count
    }

    public static ServerPacket fail(LCoinShopProduct product, byte tab) {
        return new ExPurchaseLimitShopItemBuy(product, tab,true);
    }

    public static ServerPacket success(LCoinShopProduct product, byte tab) {
        return new ExPurchaseLimitShopItemBuy(product, tab, false);
    }
}
