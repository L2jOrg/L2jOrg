package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ShowMiniMap extends IClientOutgoingPacket {
    private final int _mapId;

    public ShowMiniMap(int mapId) {
        _mapId = mapId;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.SHOW_MINIMAP.writeId(packet);

        packet.putInt(_mapId);
        packet.put((byte) 0x00); // Seven Signs state
    }
}
