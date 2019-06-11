package org.l2j.authserver.network.gameserver.packet.auth2game;

import org.l2j.authserver.network.gameserver.ServerClient;

import java.nio.ByteBuffer;

public class RequestServerIdentity extends GameServerWritablePacket {
    @Override
    protected void writeImpl(ServerClient client) {
        writeByte((byte)0x06);
    }

}
