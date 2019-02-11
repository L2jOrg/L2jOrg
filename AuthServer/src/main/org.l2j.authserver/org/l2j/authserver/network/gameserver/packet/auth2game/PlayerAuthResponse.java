package org.l2j.authserver.network.gameserver.packet.auth2game;

import org.l2j.authserver.network.gameserver.ServerClient;

import java.nio.ByteBuffer;

public class PlayerAuthResponse extends GameServerWritablePacket {

	private final String account;
	private final int response;

	public PlayerAuthResponse(String account, int response) {
		this.account = account;
		this.response = response;
	}

	@Override
	protected void writeImpl(ServerClient client, ByteBuffer buffer)  {
		buffer.put((byte)0x02);
		writeString(account, buffer);
		buffer.put((byte)response);
	}

	@Override
	protected int size(ServerClient client) {
		return super.size(client) + 4 + 2 * account.length();
	}
}