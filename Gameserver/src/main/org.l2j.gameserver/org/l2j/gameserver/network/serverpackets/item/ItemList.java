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
package org.l2j.gameserver.network.serverpackets.item;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
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
        Collection<Item> items = player.getInventory().getItems(item -> !item.isQuestItem());
        sendHeader(player, show, items.size());
        sendList(player, items);
    }

    private static void sendList(Player player, Collection<Item> items) {
        player.sendPacket(new List(items));
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
        protected void writeImpl(GameClient client) {
            writeId(ServerPacketId.ITEMLIST);
            writeByte(ItemPacketType.HEADER.clientId());
            writeShort(show);
            writeShort(0x00); // special item count
            writeInt(itemsAmount);
        }
    }

    private static final class List extends AbstractItemPacket {

        private final Collection<Item> items;

        private List(Collection<Item> items) {
            this.items = items;
        }

        @Override
        protected void writeImpl(GameClient client) {
            writeId(ServerPacketId.ITEMLIST);
            writeByte(ItemPacketType.LIST.clientId());
            writeInt(items.size());
            writeInt(items.size());

            final var player = client.getPlayer();

            for (Item item : items) {
                writeItem(item, player);
            }
        }
    }
}
