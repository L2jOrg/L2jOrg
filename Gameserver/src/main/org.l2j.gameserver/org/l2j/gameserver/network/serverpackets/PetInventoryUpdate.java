package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.ItemInfo;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.List;

/**
 * @author Yme, Advi, UnAfraid
 */
public class PetInventoryUpdate extends AbstractInventoryUpdate {
    public PetInventoryUpdate() {
    }

    public PetInventoryUpdate(Item item) {
        super(item);
    }

    public PetInventoryUpdate(List<ItemInfo> items) {
        super(items);
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.PET_INVENTORY_UPDATE);

        writeItems();
    }
}
