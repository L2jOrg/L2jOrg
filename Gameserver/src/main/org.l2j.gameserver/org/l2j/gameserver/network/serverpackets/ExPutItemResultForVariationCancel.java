package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ExPutItemResultForVariationCancel extends ServerPacket {
    private final int _itemObjId;
    private final int _itemId;
    private final int _itemAug1;
    private final int _itemAug2;
    private final long _price;

    public ExPutItemResultForVariationCancel(Item item, long price) {
        _itemObjId = item.getObjectId();
        _itemId = item.getDisplayId();
        _price = price;
        _itemAug1 = item.getAugmentation().getOption1Id();
        _itemAug2 = item.getAugmentation().getOption2Id();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_PUT_ITEM_RESULT_FOR_VARIATION_CANCEL);

        writeInt(_itemObjId);
        writeInt(_itemId);
        writeInt(_itemAug1);
        writeInt(_itemAug2);
        writeLong(_price);
        writeInt(0x01);
    }

}
