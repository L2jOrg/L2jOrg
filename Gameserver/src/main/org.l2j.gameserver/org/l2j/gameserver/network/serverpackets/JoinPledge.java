package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class JoinPledge extends ServerPacket {
    private final int _pledgeId;

    public JoinPledge(int pledgeId) {
        _pledgeId = pledgeId;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.JOIN_PLEDGE);

        writeInt(_pledgeId);
    }

}
