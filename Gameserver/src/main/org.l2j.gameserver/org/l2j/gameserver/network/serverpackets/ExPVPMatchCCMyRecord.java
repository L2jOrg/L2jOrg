package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author Mobius
 */
public class ExPVPMatchCCMyRecord extends ServerPacket {
    private final int _points;

    public ExPVPMatchCCMyRecord(int points) {
        _points = points;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_PVPMATCH_CC_MY_RECORD);
        writeInt(_points);
    }

}
