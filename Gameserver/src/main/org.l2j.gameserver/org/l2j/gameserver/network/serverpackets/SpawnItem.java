package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class SpawnItem extends IClientOutgoingPacket {
    private final L2ItemInstance _item;

    public SpawnItem(L2ItemInstance item) {
        _item = item;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.SPAWN_ITEM);

        writeInt(_item.getObjectId());
        writeInt(_item.getDisplayId());
        writeInt(_item.getX());
        writeInt(_item.getY());
        writeInt(_item.getZ());
        // only show item count if it is a stackable item
        writeInt(_item.isStackable() ? 0x01 : 0x00);
        writeLong(_item.getCount());
        writeInt(0x00); // c2
        writeByte((byte) _item.getEnchantLevel()); // Grand Crusade
        writeByte((byte) (_item.getAugmentation() != null ? 1 : 0)); // Grand Crusade
        writeByte((byte) _item.getSpecialAbilities().size()); // Grand Crusade
    }

}
