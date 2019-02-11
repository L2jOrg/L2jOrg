package org.l2j.authserver.network.gameserver.packet.auth2game;

import org.l2j.authserver.network.gameserver.ServerClient;

import java.nio.ByteBuffer;

public class RequestAccountInfo extends GameServerWritablePacket {

    private final String account;

    public RequestAccountInfo(String account) {
        this.account = account;
    }

    @Override
    protected void writeImpl(ServerClient client, ByteBuffer buffer) {
        buffer.put((byte)0x04);
        writeString(account, buffer);
    }

    @Override
    protected int size(ServerClient client) {
        return super.size(client) + 3 + 2 * account.length();
    }
}
