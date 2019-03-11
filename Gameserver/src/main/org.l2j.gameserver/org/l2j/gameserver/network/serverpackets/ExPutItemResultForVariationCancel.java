package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ExPutItemResultForVariationCancel extends IClientOutgoingPacket {
    private final int _itemObjId;
    private final int _itemId;
    private final int _itemAug1;
    private final int _itemAug2;
    private final long _price;

    public ExPutItemResultForVariationCancel(L2ItemInstance item, long price) {
        _itemObjId = item.getObjectId();
        _itemId = item.getDisplayId();
        _price = price;
        _itemAug1 = item.getAugmentation().getOption1Id();
        _itemAug2 = item.getAugmentation().getOption2Id();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_PUT_ITEM_RESULT_FOR_VARIATION_CANCEL.writeId(packet);

        packet.putInt(_itemObjId);
        packet.putInt(_itemId);
        packet.putInt(_itemAug1);
        packet.putInt(_itemAug2);
        packet.putLong(_price);
        packet.putInt(0x01);
    }
}
