package org.l2j.authserver.network.client.packet.auth2client;

import org.l2j.authserver.network.client.AuthClient;
import org.l2j.authserver.network.client.packet.L2LoginServerPacket;

import java.nio.ByteBuffer;

public final class PlayOk extends L2LoginServerPacket {
    private final int serverId;

    public PlayOk(int serverId) {
        this.serverId = serverId;
    }

    @Override
    protected void writeImpl(AuthClient client, ByteBuffer buffer) {
        var sessionKey = client.getSessionKey();
        buffer.put((byte)0x07);
        buffer.putInt(sessionKey.getGameServerSessionId());
        buffer.putInt(sessionKey.getGameServerAccountId());
        buffer.put((byte)serverId);
    }

    @Override
    protected int size(AuthClient client) {
        return super.size(client) + 10;
    }
}
