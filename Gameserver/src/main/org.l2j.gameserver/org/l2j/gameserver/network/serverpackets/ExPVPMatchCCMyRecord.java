package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Mobius
 */
public class ExPVPMatchCCMyRecord extends IClientOutgoingPacket {
    private final int _points;

    public ExPVPMatchCCMyRecord(int points) {
        _points = points;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_PVP_MATCH_CCMY_RECORD);
        writeInt(_points);
    }

}
