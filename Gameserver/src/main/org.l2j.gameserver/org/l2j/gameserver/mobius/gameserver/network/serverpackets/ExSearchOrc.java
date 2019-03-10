package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author -Wooden-
 */
@StaticPacket
public class ExSearchOrc extends IClientOutgoingPacket {
    public static final ExSearchOrc STATIC_PACKET = new ExSearchOrc();

    private ExSearchOrc() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SEARCH_ORC.writeId(packet);

    }
}
