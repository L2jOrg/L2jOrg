package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class ChooseInventoryItem extends ServerPacket {
    private final int _itemId;

    public ChooseInventoryItem(int itemId) {
        _itemId = itemId;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.CHOOSE_INVENTORY_ITEM);

        writeInt(_itemId);
    }

}
