package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

@StaticPacket
public final class LeaveWorld extends IClientOutgoingPacket {
    public static final LeaveWorld STATIC_PACKET = new LeaveWorld();

    private LeaveWorld() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.LOG_OUT_OK.writeId(packet);
    }

    @Override
    protected int size(L2GameClient client) {
        return 5;
    }
}
