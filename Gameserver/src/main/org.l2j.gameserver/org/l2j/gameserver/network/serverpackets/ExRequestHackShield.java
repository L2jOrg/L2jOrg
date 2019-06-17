package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * Trigger packet
 *
 * @author KenM
 */
@StaticPacket
public class ExRequestHackShield extends ServerPacket {
    public static final ExRequestHackShield STATIC_PACKET = new ExRequestHackShield();

    private ExRequestHackShield() {
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_REQUEST_HACK_SHIELD);

    }

}
