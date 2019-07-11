package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class GetItem extends ServerPacket {
    private final Item _item;
    private final int _playerId;

    public GetItem(Item item, int playerId) {
        _item = item;
        _playerId = playerId;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.GET_ITEM);

        writeInt(_playerId);
        writeInt(_item.getObjectId());

        writeInt(_item.getX());
        writeInt(_item.getY());
        writeInt(_item.getZ());
    }

}
