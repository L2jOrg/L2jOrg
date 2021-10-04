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
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author JoeAlisson
 */
public class RecipeShopSellList extends ServerPacket {
    private final Player buyer;
    private final Player manufacturer;

    public RecipeShopSellList(Player buyer, Player manufacturer) {
        this.buyer = buyer;
        this.manufacturer = manufacturer;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.RECIPE_SHOP_SELL_LIST, buffer );

        buffer.writeInt(manufacturer.getObjectId());
        buffer.writeInt((int) manufacturer.getCurrentMp());
        buffer.writeInt(manufacturer.getMaxMp());
        buffer.writeLong(buyer.getAdena());

        var items = manufacturer.getManufactureItems();
        buffer.writeInt(items.size());

        for (var item : items.values()) {
            buffer.writeInt(item.getRecipeId());
            buffer.writeInt(0x00); // unknown
            buffer.writeLong(item.getPrice());

            buffer.writeDouble(manufacturer.getStats().getValue(Stat.CRAFT_RATE_MASTER));
            buffer.writeByte(0x01); // show crit rate
            buffer.writeDouble(manufacturer.getStats().getValue(Stat.CRAFT_RATE_CRITICAL));
        }
    }

}
