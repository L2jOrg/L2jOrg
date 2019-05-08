package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author devScarlet, mrTJO
 */
@StaticPacket
public class ServerClose extends IClientOutgoingPacket {
    public static final ServerClose STATIC_PACKET = new ServerClose();

    private ServerClose() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.SEVER_CLOSE.writeId(packet);
    }

    @Override
    protected int size(L2GameClient client) {
        return 5;
    }
}
