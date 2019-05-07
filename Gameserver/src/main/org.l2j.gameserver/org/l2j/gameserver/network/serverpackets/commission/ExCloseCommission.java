package org.l2j.gameserver.network.serverpackets.commission;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author NosBit
 */
@StaticPacket
public class ExCloseCommission extends IClientOutgoingPacket {
    public static final ExCloseCommission STATIC_PACKET = new ExCloseCommission();

    private ExCloseCommission() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_CLOSE_COMMISSION.writeId(packet);
    }

    @Override
    protected int size(L2GameClient client) {
        return 5;
    }
}
