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
