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
package org.l2j.gameserver.network.serverpackets.l2coin;

import io.github.joealisson.mmocore.WritableBuffer;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.engine.item.shop.LCoinShop;
import org.l2j.gameserver.engine.item.shop.lcoin.LCoinShopProduct;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.pledge.ExPledgeCoinInfo;

import java.util.List;

/**
 * @author JoeAlisson
 */
public class ExPurchaseLimitShopItemListNew extends ServerPacket
{
    private final byte _index;

    public ExPurchaseLimitShopItemListNew(byte index)
    {
        _index = index;
    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer)
    {
        final IntMap<LCoinShopProduct> products = LCoinShop.getInstance().getProducts(_index);
        if (products == null)
        {
            return;
        }
        final Player player = client.getPlayer();
        if (_index == 100)
        {
            client.sendPacket(new ExPledgeCoinInfo(player));
        }
        writeId(ServerExPacketId.EX_PURCHASE_LIMIT_SHOP_ITEM_LIST_NEW, buffer);
        buffer.writeByte(_index);
        buffer.writeByte(0); // cPage
        buffer.writeByte(0); // cMaxPage
        buffer.writeInt(products.size());
        for (var product : products.values())
        {
            buffer.writeInt(product.id());
            buffer.writeInt(product.productions().get(0).getId());
            writeIngredients(product.ingredients(), buffer);
            if (product.restrictionAmount() > 0)
            {
                buffer.writeInt(product.restrictionAmount() - LCoinShop.getInstance().boughtCount(player, product));
            }
            else
            {
                buffer.writeInt(1);
            }
            buffer.writeInt(product.remainTime());
            buffer.writeInt(product.remainServerItemAmount());
            buffer.writeShort(0); // sCircleNum
        }
    }

    private void writeIngredients(List<ItemHolder> ingredients, WritableBuffer buffer)
    {
        final int size = ingredients.size();
        for (int i = 0; i < 5; i++)
        {
            if (i < size)
            {
                final int id = ingredients.get(i).getId();
                buffer.writeInt(id == CommonItem.HONOR_COIN ? -700 : id);
            }
            else
            {
                buffer.writeInt(0);
            }
        }
        for (int i = 0; i < 5; i++)
        {
            if (i < size)
            {
                buffer.writeLong(ingredients.get(i).getCount());
            }
            else
            {
                buffer.writeLong(0);
            }
        }
        for (int i = 0; i < 5; i++)
        {
            if (i < size)
            {
                buffer.writeShort(ingredients.get(i).getEnchantment());
            }
            else
            {
                buffer.writeShort(0);
            }
        }
    }
}
