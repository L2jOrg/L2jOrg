package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author devScarlet, mrTJO
 */
@StaticPacket
public class ServerClose extends ServerPacket {
    public static final ServerClose STATIC_PACKET = new ServerClose();

    private ServerClose() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.SEVER_CLOSE);
    }

}
