package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class AskJoinPledge extends IClientOutgoingPacket {
    private final L2PcInstance _requestor;
    private final int _pledgeType;
    private final String _pledgeName;

    public AskJoinPledge(L2PcInstance requestor, int pledgeType, String pledgeName) {
        _requestor = requestor;
        _pledgeType = pledgeType;
        _pledgeName = pledgeName;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.ASK_JOIN_PLEDGE);
        writeInt(_requestor.getObjectId());
        writeString(_requestor.getName());
        writeString(_pledgeName);
        if (_pledgeType != 0) {
            writeInt(_pledgeType);
        }
    }

}
