package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class AskJoinPledge extends ServerPacket {
    private final Player _requestor;
    private final int _pledgeType;
    private final String _pledgeName;

    public AskJoinPledge(Player requestor, int pledgeType, String pledgeName) {
        _requestor = requestor;
        _pledgeType = pledgeType;
        _pledgeName = pledgeName;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.ASK_JOIN_PLEDGE);
        writeInt(_requestor.getObjectId());
        writeString(_requestor.getName());
        writeString(_pledgeName);
        if (_pledgeType != 0) {
            writeInt(_pledgeType);
        }
    }

}
