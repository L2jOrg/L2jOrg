package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

@StaticPacket
public class SunSet extends IClientOutgoingPacket {
    public static final SunSet STATIC_PACKET = new SunSet();

    private SunSet() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.SUN_SET.writeId(packet);
    }
}
