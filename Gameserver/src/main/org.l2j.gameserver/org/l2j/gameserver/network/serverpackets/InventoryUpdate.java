package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.ItemInfo;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.List;

/**
 * @author Advi, UnAfraid
 */
public class InventoryUpdate extends AbstractInventoryUpdate {
    public InventoryUpdate() {
    }

    public InventoryUpdate(L2ItemInstance item) {
        super(item);
    }

    public InventoryUpdate(List<ItemInfo> items) {
        super(items);
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.INVENTORY_UPDATE);

        writeItems();
    }
}
