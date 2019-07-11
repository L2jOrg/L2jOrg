package org.l2j.gameserver.network.serverpackets.commission;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author NosBit
 */
@StaticPacket
public class ExShowCommission extends ServerPacket {
    public static final ExShowCommission STATIC_PACKET = new ExShowCommission();

    private ExShowCommission() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_SHOW_COMMISSION);

        writeInt(0x01);
    }

}
