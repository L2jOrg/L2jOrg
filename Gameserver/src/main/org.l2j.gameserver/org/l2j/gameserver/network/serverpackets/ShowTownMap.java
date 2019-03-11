package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ShowTownMap extends IClientOutgoingPacket {
    private final String _texture;
    private final int _x;
    private final int _y;

    public ShowTownMap(String texture, int x, int y) {
        _texture = texture;
        _x = x;
        _y = y;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.SHOW_TOWN_MAP.writeId(packet);
        writeString(_texture, packet);
        packet.putInt(_x);
        packet.putInt(_y);
    }
}
