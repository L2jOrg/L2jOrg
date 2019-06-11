package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * Asks the player to join a CC
 *
 * @author chris_00
 */
public class ExAskJoinMPCC extends IClientOutgoingPacket {
    private final String _requestorName;

    public ExAskJoinMPCC(String requestorName) {
        _requestorName = requestorName;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_ASK_JOIN_MPCC);

        writeString(_requestorName); // name of CCLeader
        writeInt(0x00); // TODO: Find me
    }

}
