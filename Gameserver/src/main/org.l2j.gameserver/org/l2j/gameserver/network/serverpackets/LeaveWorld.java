package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

@StaticPacket
public final class LeaveWorld extends ServerPacket {
    public static final LeaveWorld STATIC_PACKET = new LeaveWorld();

    private LeaveWorld() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.LOG_OUT_OK);
    }

}
