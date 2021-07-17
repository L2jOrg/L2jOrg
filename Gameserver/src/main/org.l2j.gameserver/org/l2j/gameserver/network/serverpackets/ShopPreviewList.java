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
package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.engine.item.ItemTemplate;
import org.l2j.gameserver.model.buylist.Product;
import org.l2j.gameserver.model.buylist.ProductList;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.settings.GeneralSettings;

public class ShopPreviewList extends ServerPacket {
    private final long money;
    private final ProductList list;

    public ShopPreviewList(ProductList list, long currentMoney) {
        this.list = list;
        money = currentMoney;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.BUY_PREVIEW_LIST, buffer );

        buffer.writeInt(5056);
        buffer.writeLong(money);
        buffer.writeInt(list.getListId());

        int newLength = 0;
        for (Product product : list.getProducts()) {
            if (product.isEquipable()) {
                newLength++;
            }
        }
        buffer.writeShort(newLength);

        for (Product product : list.getProducts()) {
            if (product.isEquipable()) {
                buffer.writeInt(product.getItemId());
                buffer.writeShort(product.getType2());

                if (product.getType1() != ItemTemplate.TYPE1_ITEM_QUESTITEM_ADENA) {
                    buffer.writeLong(product.getBodyPart().getId());
                } else {
                    buffer.writeLong(0x00);
                }
                buffer.writeLong(GeneralSettings.wearPrice());
            }
        }
    }
}
