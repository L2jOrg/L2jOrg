package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.ItemInfo;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Collection;
import java.util.List;

/**
 * @author Advi, UnAfraid
 */
public class InventoryUpdate extends AbstractInventoryUpdate {
    public InventoryUpdate() {
    }

    public InventoryUpdate(Item item) {
        super(item);
    }

    public InventoryUpdate(List<ItemInfo> items) {
        super(items);
    }

    public InventoryUpdate(Collection<Item> items) {
        super(items);
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.INVENTORY_UPDATE);

        writeItems();
    }
}
