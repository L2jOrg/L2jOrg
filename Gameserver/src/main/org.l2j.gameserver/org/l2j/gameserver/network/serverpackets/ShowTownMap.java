package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ShowTownMap extends ServerPacket {
    private final String _texture;
    private final int _x;
    private final int _y;

    public ShowTownMap(String texture, int x, int y) {
        _texture = texture;
        _x = x;
        _y = y;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.SHOW_TOWNMAP);
        writeString(_texture);
        writeInt(_x);
        writeInt(_y);
    }

}
