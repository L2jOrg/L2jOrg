package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * Opens the CommandChannel Information window
 *
 * @author chris_00
 */
public class ExOpenMPCC extends IClientOutgoingPacket {
    public static final ExOpenMPCC STATIC_PACKET = new ExOpenMPCC();

    private ExOpenMPCC() {
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_OPEN_MPCC);
    }

}
