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

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.engine.item.shop.LCoinShop;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.List;

/**
 * @author JoeAlisson
 */
public class ExPurchaseLimitShopItemListNew extends ServerPacket {

    private final byte index;

    public ExPurchaseLimitShopItemListNew(byte index) {
        this.index = index;
    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer)  {
        writeId(ServerExPacketId.EX_PURCHASE_LIMIT_SHOP_ITEM_LIST_NEW, buffer );
        buffer.writeByte(index);

        final var products = LCoinShop.getInstance().getProductInfos();
        buffer.writeInt(products.size());

        for (var product : products.values()) {
            buffer.writeInt(product.id());
            buffer.writeInt(product.production().getId());
            writeIngredients(product.ingredients(), buffer);
            buffer.writeInt(product.restrictionAmount() - LCoinShop.getInstance().boughtCount(client.getPlayer(), product));
            buffer.writeShort(0x00);
            buffer.writeByte(-1);
            buffer.writeByte(-1);
            buffer.writeByte(-1);
            buffer.writeByte(-1);
            buffer.writeInt(product.remainTime());
            buffer.writeInt(product.remainServerItemAmount());

        }
    }

    private void writeIngredients(List<ItemHolder> ingredients, WritableBuffer buffer) {
        for (int i = 0; i < 3; i++) {
            if(i < ingredients.size()) {
                buffer.writeInt(ingredients.get(i).getId());
            } else {
                buffer.writeInt(0);
            }
        }

        for (int i = 0; i < 3; i++) {
            if(i < ingredients.size()) {
                buffer.writeLong(ingredients.get(i).getCount());
            } else {
                buffer.writeLong(0);
            }

        }
    }
}
