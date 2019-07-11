package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ExPutItemResultForVariationMake extends ServerPacket {
    private final int _itemObjId;
    private final int _itemId;

    public ExPutItemResultForVariationMake(int itemObjId, int itemId) {
        _itemObjId = itemObjId;
        _itemId = itemId;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_PUT_ITEM_RESULT_FOR_VARIATION_MAKE);

        writeInt(_itemObjId);
        writeInt(_itemId);
        writeInt(0x01);
    }

}
