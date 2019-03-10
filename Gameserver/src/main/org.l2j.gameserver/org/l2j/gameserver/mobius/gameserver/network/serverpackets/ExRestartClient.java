package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author -Wooden-
 */
@StaticPacket
public class ExRestartClient extends IClientOutgoingPacket {
    public static final ExRestartClient STATIC_PACKET = new ExRestartClient();

    private ExRestartClient() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_RESTART_CLIENT.writeId(packet);
    }
}
