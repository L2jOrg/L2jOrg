package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.ASK_JOIN_PLEDGE.writeId(packet);
        packet.putInt(_requestor.getObjectId());
        writeString(_requestor.getName(), packet);
        writeString(_pledgeName, packet);
        if (_pledgeType != 0) {
            packet.putInt(_pledgeType);
        }
    }
}
