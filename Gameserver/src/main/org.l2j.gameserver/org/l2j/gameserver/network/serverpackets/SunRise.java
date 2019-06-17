package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

@StaticPacket
public class SunRise extends ServerPacket {
    public static final SunRise STATIC_PACKET = new SunRise();

    private SunRise() {
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.SUN_RISE);
    }

}
