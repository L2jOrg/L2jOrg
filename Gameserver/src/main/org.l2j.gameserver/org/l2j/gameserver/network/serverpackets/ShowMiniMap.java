package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ShowMiniMap extends IClientOutgoingPacket {
    private final int _mapId;

    public ShowMiniMap(int mapId) {
        _mapId = mapId;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.SHOW_MINIMAP);

        writeInt(_mapId);
        writeByte((byte) 0x00); // Seven Signs state
    }

}
