package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

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
        writeId(ServerPacketId.EX_PVP_MATCH_CCMY_RECORD);
        writeInt(_points);
    }

}
