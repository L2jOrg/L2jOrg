package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.ItemInfo;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author Yme, Advi, UnAfraid
 */
public class PetInventoryUpdate extends AbstractInventoryUpdate {
    public PetInventoryUpdate() {
    }

    public PetInventoryUpdate(L2ItemInstance item) {
        super(item);
    }

    public PetInventoryUpdate(List<ItemInfo> items) {
        super(items);
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.PET_INVENTORY_UPDATE);

        writeItems();
    }
}
