package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class JoinPledge extends ServerPacket {
    private final int _pledgeId;

    public JoinPledge(int pledgeId) {
        _pledgeId = pledgeId;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.JOIN_PLEDGE);

        writeInt(_pledgeId);
    }

}
