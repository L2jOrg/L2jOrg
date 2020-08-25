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
package org.l2j.gameserver.network.serverpackets.store;

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
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_BR_PRODUCT_LIST_ACK);

        writeLong(player.getAdena());
        writeLong(0x00); // Hero coins
        writeByte(type); // Type 0 - Home, 1 - History, 2 - Favorites
        writeInt(products.size());

        for (L2StoreProduct brItem : products) {
            writeProduct(brItem);
        }
    }

    private void writeProduct(L2StoreProduct product) {
        writeInt(product.getId());
        writeByte(product.getCategory());
        writeByte(product.getPaymentType());
        writeInt(product.getPrice());
        writeByte(product.getPanelType()); // Item Panel Type: 0 - None, 1 - Event, 2 - Sale, 3 - New, 4 - Best
        writeInt(product.getRecommended()); // Recommended: (bit flags) 1 - Top, 2 - Left, 4 - Right
        writeInt(product.getStartSale());
        writeInt(product.getEndSale());
        writeByte(product.getDaysOfWeek());
        writeByte(product.getStartHour());
        writeByte(product.getStartMinute());
        writeByte(product.getStopHour());
        writeByte(product.getStopMinute());
        writeInt(product.getStock());
        writeInt(product.getMaxStock());
        writeByte(product.getSalePercent());
        writeByte(product.getMinLevel());
        writeByte(product.getMaxLevel());
        writeInt(product.getMinBirthday());
        writeInt(product.getMaxBirthday());
        writeInt(product.getRestrictionAmount());
        writeInt(product.getAvailableCount());
        writeByte(product.getItems().size());
        for (L2StoreItem item : product.getItems()) {
            writeItem(item);
        }
    }

    private void writeItem(L2StoreItem item) {
        writeInt(item.getId());
        writeInt((int) item.getCount());
        writeInt(item.getWeight());
        writeInt(item.isTradable());
    }
}