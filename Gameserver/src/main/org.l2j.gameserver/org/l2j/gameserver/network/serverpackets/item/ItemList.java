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
package org.l2j.gameserver.network.serverpackets.item;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.AbstractItemPacket;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.Collection;

/**
 * @author JoeAlisson
 */
public final class ItemList {

    public static void show(Player player) {
        sendPackets(player, true);
    }

    public static void sendList(Player player) {
        sendPackets(player, false);
    }

    private static void sendPackets(Player player, boolean show) {
        Collection<Item> items = player.getInventory().getItems();
        sendHeader(player, show, items.size());
        sendContent(player, items);
    }

    private static void sendContent(Player player, Collection<Item> items) {
        player.sendPacket(new Content(items));
    }

    private static void sendHeader(Player player, boolean show, int itemsAmount) {
        player.sendPacket(new Header(show, itemsAmount));
    }

    private static class Header extends ServerPacket {

        private final boolean show;
        private final int itemsAmount;

        public Header(boolean show, int itemsAmount) {
            this.show = show;
            this.itemsAmount = itemsAmount;
        }

        @Override
        protected void writeImpl(GameClient client, WritableBuffer buffer) {
            writeId(ServerPacketId.ITEMLIST, buffer );
            buffer.writeByte(ItemPacketType.HEADER.clientId());
            buffer.writeShort(show);
            buffer.writeShort(0x00); // special item count
            buffer.writeInt(itemsAmount);
        }
    }

    private static final class Content extends AbstractItemPacket {

        private final Collection<Item> items;

        private Content(Collection<Item> items) {
            this.items = items;
        }

        @Override
        protected void writeImpl(GameClient client, WritableBuffer buffer) {
            writeId(ServerPacketId.ITEMLIST, buffer );
            buffer.writeByte(ItemPacketType.LIST.clientId());
            buffer.writeInt(items.size());
            buffer.writeInt(items.size());

            final var player = client.getPlayer();

            for (Item item : items) {
                writeItem(item, player, buffer);
            }
        }
    }
}
