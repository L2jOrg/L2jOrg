package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * Close the CommandChannel Information window
 *
 * @author chris_00
 */
@StaticPacket
public class ExCloseMPCC extends ServerPacket {
    public static final ExCloseMPCC STATIC_PACKET = new ExCloseMPCC();

    private ExCloseMPCC() {
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_CLOSE_MPCC);

    }

}
