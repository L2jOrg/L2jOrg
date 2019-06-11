package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ExPutItemResultForVariationMake extends IClientOutgoingPacket {
    private final int _itemObjId;
    private final int _itemId;

    public ExPutItemResultForVariationMake(int itemObjId, int itemId) {
        _itemObjId = itemObjId;
        _itemId = itemId;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_PUT_ITEM_RESULT_FOR_VARIATION_MAKE);

        writeInt(_itemObjId);
        writeInt(_itemId);
        writeInt(0x01);
    }

}
