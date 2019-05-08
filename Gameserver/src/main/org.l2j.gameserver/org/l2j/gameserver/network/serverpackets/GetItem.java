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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.GET_ITEM.writeId(packet);

        packet.putInt(_playerId);
        packet.putInt(_item.getObjectId());

        packet.putInt(_item.getX());
        packet.putInt(_item.getY());
        packet.putInt(_item.getZ());
    }

    @Override
    protected int size(L2GameClient client) {
        return 25;
    }
}
