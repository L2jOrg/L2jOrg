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
import org.l2j.gameserver.engine.item.shop.lcoin.LCoinShopProduct;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.Map;

/**
 * @author JoeAlisson
 */
public class ExPurchaseLimitShopItemBuy extends ServerPacket {

    private final LCoinShopProduct      _product;
    private final Map<Integer, Integer> _productions;
    private final byte                  _index;
    private final boolean               _fail;

    private ExPurchaseLimitShopItemBuy(LCoinShopProduct product, Map<Integer, Integer> productions, byte index, boolean fail)
    {
        _product = product;
        _productions = productions;
        _index = index;
        _fail = fail;
    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer)
    {
        writeId(ServerExPacketId.EX_PURCHASE_LIMIT_SHOP_ITEM_BUY, buffer);
        buffer.writeByte(_fail);
        buffer.writeByte(_index);
        buffer.writeInt(_product.id());
        if (_fail)
        {
            buffer.writeInt(0); // size
        }
        else
        {
            buffer.writeInt(_productions.size()); // size
            int i = 0;
            for (Map.Entry<Integer, Integer> map : _productions.entrySet())
            {
                buffer.writeByte(i++);
                buffer.writeInt(map.getKey()); // item id
                buffer.writeInt(map.getValue()); // count
            }
        }
        buffer.writeInt(_product.getRemainAmount()); // remain item count
    }

    public static ServerPacket fail(LCoinShopProduct product, byte index)
    {
        return new ExPurchaseLimitShopItemBuy(product, null, index, true);
    }

    public static ServerPacket success(LCoinShopProduct product, Map<Integer, Integer> productions, byte index)
    {
        return new ExPurchaseLimitShopItemBuy(product, productions, index, false);
    }
}
