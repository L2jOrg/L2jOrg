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
