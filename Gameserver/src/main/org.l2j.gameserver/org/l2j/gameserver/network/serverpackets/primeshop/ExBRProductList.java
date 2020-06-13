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
package org.l2j.gameserver.network.serverpackets.primeshop;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.primeshop.PrimeShopItem;
import org.l2j.gameserver.model.primeshop.PrimeShopProduct;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.Collection;

/**
 * @author UnAfraid
 */
public class ExBRProductList extends ServerPacket {
    private final Player _activeChar;
    private final int _type;
    private final Collection<PrimeShopProduct> _primeList;

    public ExBRProductList(Player activeChar, int type, Collection<PrimeShopProduct> items) {
        _activeChar = activeChar;
        _type = type;
        _primeList = items;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_BR_PRODUCT_LIST_ACK);

        writeLong(_activeChar.getAdena()); // Adena
        writeLong(0x00); // Hero coins
        writeByte((byte) _type); // Type 0 - Home, 1 - History, 2 - Favorites
        writeInt(_primeList.size());
        for (PrimeShopProduct brItem : _primeList) {
            writeInt(brItem.getId());
            writeByte(brItem.getCategory());
            writeByte(brItem.getPaymentType()); // Payment Type: 0 - Prime Points, 1 - Adena, 2 - Hero Coins
            writeInt(brItem.getPrice());
            writeByte(brItem.getPanelType()); // Item Panel Type: 0 - None, 1 - Event, 2 - Sale, 3 - New, 4 - Best
            writeInt(brItem.getRecommended()); // Recommended: (bit flags) 1 - Top, 2 - Left, 4 - Right
            writeInt(brItem.getStartSale());
            writeInt(brItem.getEndSale());
            writeByte(brItem.getDaysOfWeek());
            writeByte(brItem.getStartHour());
            writeByte(brItem.getStartMinute());
            writeByte(brItem.getStopHour());
            writeByte(brItem.getStopMinute());
            writeInt(brItem.getStock());
            writeInt(brItem.getTotal());
            writeByte(brItem.getSalePercent());
            writeByte(brItem.getMinLevel());
            writeByte(brItem.getMaxLevel());
            writeInt(brItem.getMinBirthday());
            writeInt(brItem.getMaxBirthday());
            writeInt(brItem.getRestrictionDay());
            writeInt(brItem.getAvailableCount());
            writeByte((byte) brItem.getItems().size());
            for (PrimeShopItem item : brItem.getItems()) {
                writeInt(item.getId());
                writeInt((int) item.getCount());
                writeInt(item.getWeight());
                writeInt(item.isTradable());
            }
        }
    }

}