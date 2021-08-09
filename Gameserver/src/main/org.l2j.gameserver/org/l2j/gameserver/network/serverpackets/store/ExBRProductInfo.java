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

/**
 * @author Gnacik
 */
public class ExBRProductInfo extends ServerPacket {
    private final L2StoreProduct item;
    private final int points;
    private final long adenas;
    private final long coins;

    public ExBRProductInfo(L2StoreProduct item, Player player) {
        this.item = item;
        points = player.getNCoins();
        adenas = player.getAdena();
        coins = player.getInventory().getInventoryItemCount(23805, -1);
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_BR_PRODUCT_INFO_ACK, buffer );

        buffer.writeInt(item.getId());
        buffer.writeInt(item.getPrice());
        buffer.writeInt(item.getItems().size());
        for (L2StoreItem item : item.getItems()) {
            buffer.writeInt(item.getId());
            buffer.writeInt((int) item.getCount());
            buffer.writeInt(item.getWeight());
            buffer.writeInt(item.isTradable());
        }
        buffer.writeLong(adenas);
        buffer.writeLong(points);
        buffer.writeLong(coins);
    }

}
