package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Sdw
 */
@StaticPacket
public class ExDissmissMPCCRoom extends ServerPacket {
    public static final ExDissmissMPCCRoom STATIC_PACKET = new ExDissmissMPCCRoom();

    private ExDissmissMPCCRoom() {
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_DISSMISS_MPCC_ROOM);
    }

}
