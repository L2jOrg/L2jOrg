package org.l2j.authserver.network.gameserver.packet.auth2game;

import org.l2j.authserver.network.gameserver.ServerClient;

import java.nio.ByteBuffer;

public class RequestServerIdentity extends GameServerWritablePacket {
    @Override
    protected void writeImpl(ServerClient client, ByteBuffer buffer) {
        buffer.put((byte)0x06);
    }

    @Override
    protected int size(ServerClient client) {
        return super.size(client) + 1;
    }
}
