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
public class ExShowCommission extends IClientOutgoingPacket {
    public static final ExShowCommission STATIC_PACKET = new ExShowCommission();

    private ExShowCommission() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SHOW_COMMISSION.writeId(packet);

        packet.putInt(0x01);
    }

    @Override
    protected int size(L2GameClient client) {
        return 9;
    }
}
