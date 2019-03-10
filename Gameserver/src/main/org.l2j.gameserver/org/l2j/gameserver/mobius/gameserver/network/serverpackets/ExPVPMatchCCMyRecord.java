package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_PVP_MATCH_CCMY_RECORD.writeId(packet);
        packet.putInt(_points);
    }
}
