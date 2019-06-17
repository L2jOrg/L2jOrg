package org.l2j.gameserver.network.serverpackets.commission;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author NosBit
 */
@StaticPacket
public class ExCloseCommission extends ServerPacket {
    public static final ExCloseCommission STATIC_PACKET = new ExCloseCommission();

    private ExCloseCommission() {
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_CLOSE_COMMISSION);
    }

}
