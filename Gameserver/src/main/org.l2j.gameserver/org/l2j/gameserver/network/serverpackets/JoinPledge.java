package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class JoinPledge extends IClientOutgoingPacket {
    private final int _pledgeId;

    public JoinPledge(int pledgeId) {
        _pledgeId = pledgeId;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.JOIN_PLEDGE.writeId(packet);

        packet.putInt(_pledgeId);
    }

    @Override
    protected int size(L2GameClient client) {
        return 9;
    }
}
