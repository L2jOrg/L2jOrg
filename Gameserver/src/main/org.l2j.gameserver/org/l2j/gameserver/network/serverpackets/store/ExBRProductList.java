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
package org.l2j.gameserver.network.serverpackets.store;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.engine.item.shop.l2store.L2StoreItem;
import org.l2j.gameserver.engine.item.shop.l2store.L2StoreProduct;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.Collection;

/**
 * @author UnAfraid
 */
public class ExBRProductList extends ServerPacket {
    private final Player player;
    private final int type;
    private final Collection<L2StoreProduct> products;

    public ExBRProductList(Player activeChar, int type, Collection<L2StoreProduct> items) {
        player = activeChar;
        this.type = type;
        products = items;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_BR_PRODUCT_LIST_ACK, buffer );

        buffer.writeLong(player.getAdena());
        buffer.writeLong(0x00); // Hero coins
        buffer.writeByte(type); // Type 0 - Home, 1 - History, 2 - Favorites
        buffer.writeInt(products.size());

        for (L2StoreProduct brItem : products) {
            writeProduct(brItem, buffer);
        }
    }

    private void writeProduct(L2StoreProduct product, WritableBuffer buffer) {
        buffer.writeInt(product.getId());
        buffer.writeByte(product.getCategory());
        buffer.writeByte(product.getPaymentType());
        buffer.writeInt(product.getPrice());
        buffer.writeByte(product.getPanelType()); // Item Panel Type: 0 - None, 1 - Event, 2 - Sale, 3 - New, 4 - Best
        buffer.writeInt(product.getRecommended()); // Recommended: (bit flags) 1 - Top, 2 - Left, 4 - Right
        buffer.writeInt(product.getStartSale());
        buffer.writeInt(product.getEndSale());
        buffer.writeByte(product.getDaysOfWeek());
        buffer.writeByte(product.getStartHour());
        buffer.writeByte(product.getStartMinute());
        buffer.writeByte(product.getStopHour());
        buffer.writeByte(product.getStopMinute());
        buffer.writeInt(product.getStock());
        buffer.writeInt(product.getMaxStock());
        buffer.writeByte(product.getSalePercent());
        buffer.writeByte(product.getMinLevel());
        buffer.writeByte(product.getMaxLevel());
        buffer.writeInt(product.getMinBirthday());
        buffer.writeInt(product.getMaxBirthday());
        buffer.writeInt(product.getRestrictionAmount());
        buffer.writeInt(product.getAvailableCount());
        buffer.writeByte(product.getItems().size());
        for (L2StoreItem item : product.getItems()) {
            writeItem(item, buffer);
        }
    }

    private void writeItem(L2StoreItem item, WritableBuffer buffer) {
        buffer.writeInt(item.getId());
        buffer.writeInt((int) item.getCount());
        buffer.writeInt(item.getWeight());
        buffer.writeInt(item.isTradable());
    }
}