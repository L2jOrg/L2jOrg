package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class ChooseInventoryItem extends ServerPacket {
    private final int _itemId;

    public ChooseInventoryItem(int itemId) {
        _itemId = itemId;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.CHOOSE_INVENTORY_ITEM);

        writeInt(_itemId);
    }

}
