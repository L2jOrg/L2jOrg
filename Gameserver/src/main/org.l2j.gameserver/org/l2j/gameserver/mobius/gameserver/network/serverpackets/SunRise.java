package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

@StaticPacket
public class SunRise extends IClientOutgoingPacket {
    public static final SunRise STATIC_PACKET = new SunRise();

    private SunRise() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.SUN_RISE.writeId(packet);
    }
}
