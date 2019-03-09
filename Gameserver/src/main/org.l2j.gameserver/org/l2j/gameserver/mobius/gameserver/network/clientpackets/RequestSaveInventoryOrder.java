package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.enums.ItemLocation;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Format:(ch) d[dd]
 * @author -Wooden-
 */
public final class RequestSaveInventoryOrder extends IClientIncomingPacket
{
    private List<InventoryOrder> _order;

    /** client limit */
    private static final int LIMIT = 125;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        int sz = packet.getInt();
        sz = Math.min(sz, LIMIT);
        _order = new ArrayList<>(sz);
        for (int i = 0; i < sz; i++)
        {
            final int objectId = packet.getInt();
            final int order = packet.getInt();
            _order.add(new InventoryOrder(objectId, order));
        }
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance player = client.getActiveChar();
        if (player != null)
        {
            final Inventory inventory = player.getInventory();
            for (InventoryOrder order : _order)
            {
                final L2ItemInstance item = inventory.getItemByObjectId(order.objectID);
                if ((item != null) && (item.getItemLocation() == ItemLocation.INVENTORY))
                {
                    item.setItemLocation(ItemLocation.INVENTORY, order.order);
                }
            }
        }
    }

    private static class InventoryOrder
    {
        int order;

        int objectID;

        public InventoryOrder(int id, int ord)
        {
            objectID = id;
            order = ord;
        }
    }
}
