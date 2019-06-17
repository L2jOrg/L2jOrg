package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * Opens the CommandChannel Information window
 *
 * @author chris_00
 */
public class ExOpenMPCC extends ServerPacket {
    public static final ExOpenMPCC STATIC_PACKET = new ExOpenMPCC();

    private ExOpenMPCC() {
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_OPEN_MPCC);
    }

}
