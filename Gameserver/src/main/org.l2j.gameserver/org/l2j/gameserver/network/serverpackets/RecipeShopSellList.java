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

import org.l2j.gameserver.model.ManufactureItem;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class RecipeShopSellList extends ServerPacket {
    private final Player _buyer;
    private final Player _manufacturer;

    public RecipeShopSellList(Player buyer, Player manufacturer) {
        _buyer = buyer;
        _manufacturer = manufacturer;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.RECIPE_SHOP_SELL_LIST);

        writeInt(_manufacturer.getObjectId());
        writeInt((int) _manufacturer.getCurrentMp()); // Creator's MP
        writeInt(_manufacturer.getMaxMp()); // Creator's MP
        writeLong(_buyer.getAdena()); // Buyer Adena
        if (!_manufacturer.hasManufactureShop()) {
            writeInt(0x00);
        } else {
            writeInt(_manufacturer.getManufactureItems().size());
            for (ManufactureItem temp : _manufacturer.getManufactureItems().values()) {
                writeInt(temp.getRecipeId());
                writeInt(0x00); // unknown
                writeLong(temp.getCost());

                writeLong(0x00); // Classic - 166
                writeLong(0x00); // Classic - 166
                writeByte(0x00); // Classic - 166
            }
        }
    }

}
