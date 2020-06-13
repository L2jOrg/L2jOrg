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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.item.instance.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Format:(ch) d[dd]
 *
 * @author -Wooden-
 */
public final class RequestSaveInventoryOrder extends ClientPacket {
    /**
     * client limit
     */
    private static final int LIMIT = 125;
    private List<InventoryOrder> _order;

    @Override
    public void readImpl() {
        int sz = readInt();
        sz = Math.min(sz, LIMIT);
        _order = new ArrayList<>(sz);
        for (int i = 0; i < sz; i++) {
            final int objectId = readInt();
            final int order = readInt();
            _order.add(new InventoryOrder(objectId, order));
        }
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player != null) {
            final Inventory inventory = player.getInventory();
            for (InventoryOrder order : _order) {
                final Item item = inventory.getItemByObjectId(order.objectID);
                if ((item != null) && (item.getItemLocation() == ItemLocation.INVENTORY)) {
                    item.setItemLocation(ItemLocation.INVENTORY, order.order);
                }
            }
        }
    }

    private static class InventoryOrder {
        int order;

        int objectID;

        public InventoryOrder(int id, int ord) {
            objectID = id;
            order = ord;
        }
    }
}
