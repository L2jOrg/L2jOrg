package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class DropItem extends IClientOutgoingPacket {
    private final L2ItemInstance _item;
    private final int _charObjId;

    /**
     * Constructor of the DropItem server packet
     *
     * @param item        : L2ItemInstance designating the item
     * @param playerObjId : int designating the player ID who dropped the item
     */
    public DropItem(L2ItemInstance item, int playerObjId) {
        _item = item;
        _charObjId = playerObjId;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.DROP_ITEM.writeId(packet);

        packet.putInt(_charObjId);
        packet.putInt(_item.getObjectId());
        packet.putInt(_item.getDisplayId());

        packet.putInt(_item.getX());
        packet.putInt(_item.getY());
        packet.putInt(_item.getZ());
        // only show item count if it is a stackable item
        packet.put((byte) (_item.isStackable() ? 0x01 : 0x00));
        packet.putLong(_item.getCount());

        packet.put((byte) 0x00);
        // packet.putInt(0x01); if above C == true (1) then packet.getInt()

        packet.put((byte) _item.getEnchantLevel()); // Grand Crusade
        packet.put((byte) (_item.getAugmentation() != null ? 1 : 0)); // Grand Crusade
        packet.put((byte) _item.getSpecialAbilities().size()); // Grand Crusade
    }
}
