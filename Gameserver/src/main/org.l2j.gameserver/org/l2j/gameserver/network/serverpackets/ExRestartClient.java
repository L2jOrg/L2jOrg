package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author -Wooden-
 */
@StaticPacket
public class ExRestartClient extends ServerPacket {
    public static final ExRestartClient STATIC_PACKET = new ExRestartClient();

    private ExRestartClient() {
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_RESTART_CLIENT);
    }

}
