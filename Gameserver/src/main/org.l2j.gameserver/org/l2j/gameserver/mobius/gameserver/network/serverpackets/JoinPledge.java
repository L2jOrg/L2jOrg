package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

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
}
