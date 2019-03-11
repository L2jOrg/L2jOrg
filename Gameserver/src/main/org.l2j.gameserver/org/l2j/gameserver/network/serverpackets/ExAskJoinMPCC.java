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

    /**
     * @param requestorName
     */
    public ExAskJoinMPCC(String requestorName) {
        _requestorName = requestorName;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_ASK_JOIN_MPCC.writeId(packet);

        writeString(_requestorName, packet); // name of CCLeader
        packet.putInt(0x00); // TODO: Find me
    }
}
