package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ShowMiniMap extends ServerPacket {
    private final int _mapId;

    public ShowMiniMap(int mapId) {
        _mapId = mapId;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.SHOW_MINIMAP);

        writeInt(_mapId);
        writeByte(0x00); // Seven Signs state
    }

}
