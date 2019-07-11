package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author -Wooden-
 */
@StaticPacket
public class ExSearchOrc extends ServerPacket {
    public static final ExSearchOrc STATIC_PACKET = new ExSearchOrc();

    private ExSearchOrc() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_SEARCH_ORC);

    }

}
