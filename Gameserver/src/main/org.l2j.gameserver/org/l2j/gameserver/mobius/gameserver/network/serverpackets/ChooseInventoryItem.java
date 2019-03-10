package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class ChooseInventoryItem extends IClientOutgoingPacket {
    private final int _itemId;

    public ChooseInventoryItem(int itemId) {
        _itemId = itemId;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.CHOOSE_INVENTORY_ITEM.writeId(packet);

        packet.putInt(_itemId);
    }
}
