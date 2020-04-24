package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author Sdw
 */
@StaticPacket
public class ExDissmissMPCCRoom extends ServerPacket {
    public static final ExDissmissMPCCRoom STATIC_PACKET = new ExDissmissMPCCRoom();

    private ExDissmissMPCCRoom() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_DISMISS_MPCC_ROOM);
    }

}
