package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * (just a trigger)
 *
 * @author -Wooden-
 */
@StaticPacket
public class ExMailArrived extends IClientOutgoingPacket {
    public static final ExMailArrived STATIC_PACKET = new ExMailArrived();

    private ExMailArrived() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_MAIL_ARRIVED.writeId(packet);
    }

    @Override
    protected int size(L2GameClient client) {
        return 5;
    }
}
