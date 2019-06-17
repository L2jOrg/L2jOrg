package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ShowMiniMap extends ServerPacket {
    private final int _mapId;

    public ShowMiniMap(int mapId) {
        _mapId = mapId;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.SHOW_MINIMAP);

        writeInt(_mapId);
        writeByte((byte) 0x00); // Seven Signs state
    }

}
