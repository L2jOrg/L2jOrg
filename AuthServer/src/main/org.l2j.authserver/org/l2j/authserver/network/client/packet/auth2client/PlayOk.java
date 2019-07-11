package org.l2j.authserver.network.client.packet.auth2client;

import org.l2j.authserver.network.client.AuthClient;
import org.l2j.authserver.network.client.packet.AuthServerPacket;

public final class PlayOk extends AuthServerPacket {
    private final int serverId;

    public PlayOk(int serverId) {
        this.serverId = serverId;
    }

    @Override
    protected void writeImpl(AuthClient client) {
        var sessionKey = client.getSessionKey();
        writeByte((byte)0x07);
        writeInt(sessionKey.getGameServerSessionId());
        writeInt(sessionKey.getGameServerAccountId());
        writeByte((byte)serverId);
    }

}
