package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class SpawnItem extends IClientOutgoingPacket {
    private final L2ItemInstance _item;

    public SpawnItem(L2ItemInstance item) {
        _item = item;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.SPAWN_ITEM.writeId(packet);

        packet.putInt(_item.getObjectId());
        packet.putInt(_item.getDisplayId());
        packet.putInt(_item.getX());
        packet.putInt(_item.getY());
        packet.putInt(_item.getZ());
        // only show item count if it is a stackable item
        packet.putInt(_item.isStackable() ? 0x01 : 0x00);
        packet.putLong(_item.getCount());
        packet.putInt(0x00); // c2
        packet.put((byte) _item.getEnchantLevel()); // Grand Crusade
        packet.put((byte) (_item.getAugmentation() != null ? 1 : 0)); // Grand Crusade
        packet.put((byte) _item.getSpecialAbilities().size()); // Grand Crusade
    }
}
