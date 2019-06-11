package org.l2j.authserver.network.gameserver.packet.auth2game;

import org.l2j.authserver.network.gameserver.ServerClient;

import java.nio.ByteBuffer;

public class KickPlayer extends GameServerWritablePacket {

    private final String account;

    public KickPlayer(String account) {
        this.account = account;
	}

	@Override
	protected void writeImpl(ServerClient client) {
		writeByte((byte)0x03);
		writeString(account);
	}

}