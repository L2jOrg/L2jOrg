package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

@StaticPacket
public final class RestartResponse extends ServerPacket {
    public static final RestartResponse TRUE = new RestartResponse(true);
    public static final RestartResponse FALSE = new RestartResponse(false);

    private final boolean _result;

    private RestartResponse(boolean result) {
        _result = result;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.RESTART_RESPONSE);
        writeInt(_result ? 1 : 0);
    }

}
