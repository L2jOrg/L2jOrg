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

import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.container.ItemContainer;
import org.l2j.gameserver.model.item.container.WarehouseType;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.AbstractItemPacket;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.Collection;

import static org.l2j.commons.util.Util.computeIfNonNull;
import static org.l2j.commons.util.Util.doIfNonNull;

/**
 * @author JoeAlisson
 */
public final class WarehouseDepositList {

    public static void openOfPlayer(Player player) {
        doIfNonNull(wareHouseOf(player, WarehouseType.PRIVATE), warehouse -> {
            var depositable = player.getDepositableItems(WarehouseType.PRIVATE);
            player.sendPacket(header(warehouse, WarehouseType.PRIVATE, player, depositable.size()), new DepositableList(depositable));
        });
    }

    public static void openOfClan(Player player) {
        doIfNonNull(wareHouseOf(player, WarehouseType.CLAN), warehouse -> {
            var depositable = player.getDepositableItems(WarehouseType.CLAN);
            player.sendPacket(header(warehouse, WarehouseType.CLAN, player, depositable.size()), new DepositableList(depositable));
        });
    }

    private static Header header(ItemContainer warehouse, WarehouseType type, Player player, int depositableAmount) {
        final var stackableDeposited = warehouse.getItemsId(Item::isStackable);
        return new Header(type, stackableDeposited, warehouse.getSize(), player.getAdena(), depositableAmount);
    }

    private static ItemContainer wareHouseOf(Player player, WarehouseType type) {
        return switch (type) {
            case PRIVATE -> player.getWarehouse();
            case CLAN -> computeIfNonNull(player.getClan(), Clan::getWarehouse);
            case FREIGHT -> player.getFreight();
            default -> null;
        };
    }

    private static class Header extends ServerPacket {
        private final WarehouseType type;
        private final long adenaAmount;
        private final IntSet stackableDeposited;
        private final int depositableAmount;
        private final int depositedAmount;

        private Header(WarehouseType type, IntSet stackableDeposited, int depositedAmount, long adenaAmount, int depositableAmount) {
            this.type = type;
            this.adenaAmount = adenaAmount;
            this.stackableDeposited = stackableDeposited;
            this.depositedAmount = depositedAmount;
            this.depositableAmount = depositableAmount;
        }

        @Override
        protected void writeImpl(GameClient client) {
            writeId(ServerPacketId.WAREHOUSE_DEPOSIT_LIST);
            writeByte(ItemPacketType.HEADER.clientId());
            writeShort(type.clientId());
            writeLong(adenaAmount);
            writeInt(depositedAmount);
            writeShort(stackableDeposited.size());
            stackableDeposited.forEach(this::writeInt);
            writeInt(depositableAmount);
        }
    }

    private static class DepositableList extends AbstractItemPacket {
        private final Collection<Item> depositableItems;

        public DepositableList(Collection<Item> depositableItems) {
            this.depositableItems = depositableItems;
        }

        @Override
        protected void writeImpl(GameClient client) {
            writeId(ServerPacketId.WAREHOUSE_DEPOSIT_LIST);
            writeByte(ItemPacketType.LIST.clientId());
            writeInt(depositableItems.size());
            writeInt(depositableItems.size());
            for (Item item : depositableItems) {
                writeItem(item, client.getPlayer());
                writeInt(item.getObjectId());
            }
        }
    }
}
