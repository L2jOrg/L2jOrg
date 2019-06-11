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
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.DROP_ITEM);

        writeInt(_charObjId);
        writeInt(_item.getObjectId());
        writeInt(_item.getDisplayId());

        writeInt(_item.getX());
        writeInt(_item.getY());
        writeInt(_item.getZ());
        // only show item count if it is a stackable item
        writeByte((byte) (_item.isStackable() ? 0x01 : 0x00));
        writeLong(_item.getCount());

        writeByte((byte) 0x00);
        // writeInt(0x01); if above C == true (1) then readInt()

        writeByte((byte) _item.getEnchantLevel()); // Grand Crusade
        writeByte((byte) (_item.getAugmentation() != null ? 1 : 0)); // Grand Crusade
        writeByte((byte) _item.getSpecialAbilities().size()); // Grand Crusade
    }

}
