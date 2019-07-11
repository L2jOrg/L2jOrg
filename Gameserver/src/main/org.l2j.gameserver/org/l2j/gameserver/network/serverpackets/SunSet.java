package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

@StaticPacket
public class SunSet extends ServerPacket {
    public static final SunSet STATIC_PACKET = new SunSet();

    private SunSet() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.SUN_SET);
    }

}
