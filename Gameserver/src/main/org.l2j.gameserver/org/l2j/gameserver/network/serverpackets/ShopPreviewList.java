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
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.buylist.Product;
import org.l2j.gameserver.model.buylist.ProductList;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Collection;

public class ShopPreviewList extends ServerPacket {
    private final int _listId;
    private final Collection<Product> _list;
    private final long _money;

    public ShopPreviewList(ProductList list, long currentMoney) {
        _listId = list.getListId();
        _list = list.getProducts();
        _money = currentMoney;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.BUY_PREVIEW_LIST);

        writeInt(5056);
        writeLong(_money); // current money
        writeInt(_listId);

        int newlength = 0;
        for (Product product : _list) {
            if (product.isEquipable()) {
                newlength++;
            }
        }
        writeShort((short) newlength);

        for (Product product : _list) {
            if (product.isEquipable()) {
                writeInt(product.getItemId());
                writeShort(product.getType2()); // item type2

                if (product.getType1() != ItemTemplate.TYPE1_ITEM_QUESTITEM_ADENA) {
                    writeLong(product.getBodyPart().getId()); // rev 415 slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
                } else {
                    writeLong(0x00); // rev 415 slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
                }

                writeLong(Config.WEAR_PRICE);
            }
        }
    }

}
