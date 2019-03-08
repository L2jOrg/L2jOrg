package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.ItemInfo;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.util.List;

/**
 * @author Advi, UnAfraid
 */
public class InventoryUpdate extends AbstractInventoryUpdate
{
    public InventoryUpdate()
    {
    }

    public InventoryUpdate(L2ItemInstance item)
    {
        super(item);
    }

    public InventoryUpdate(List<ItemInfo> items)
    {
        super(items);
    }

    @Override
    public boolean write(PacketWriter packet)
    {
        OutgoingPackets.INVENTORY_UPDATE.writeId(packet);

        writeItems(packet);
        return true;
    }
}
