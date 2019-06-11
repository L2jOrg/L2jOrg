package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class GetItem extends IClientOutgoingPacket {
    private final L2ItemInstance _item;
    private final int _playerId;

    public GetItem(L2ItemInstance item, int playerId) {
        _item = item;
        _playerId = playerId;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.GET_ITEM);

        writeInt(_playerId);
        writeInt(_item.getObjectId());

        writeInt(_item.getX());
        writeInt(_item.getY());
        writeInt(_item.getZ());
    }

}
