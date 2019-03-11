package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

@StaticPacket
public final class RestartResponse extends IClientOutgoingPacket {
    public static final RestartResponse TRUE = new RestartResponse(true);
    public static final RestartResponse FALSE = new RestartResponse(false);

    private final boolean _result;

    private RestartResponse(boolean result) {
        _result = result;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.RESTART_RESPONSE.writeId(packet);
        packet.putInt(_result ? 1 : 0);
    }
}
