package org.l2j.authserver.network.gameserver.packet.auth2game;

import org.l2j.authserver.controller.AuthController;
import org.l2j.authserver.network.gameserver.ServerClient;

import java.nio.ByteBuffer;

public class PlayerAuthResponse extends GameServerWritablePacket {

	private final String account;
	private final boolean response;

	public PlayerAuthResponse(String account, boolean response) {
		this.account = account;
		this.response = response;
	}

	@Override
	protected void writeImpl(ServerClient client, ByteBuffer buffer)  {
		buffer.put((byte)0x02);
		writeString(account, buffer);
		buffer.put((byte) (response ? 0x01 : 0x00));
		if(response) {
			var key  = AuthController.getInstance().getKeyForAccount(account);
			buffer.putInt(key.getGameServerSessionId());
			buffer.putInt(key.getGameServerAccountId());
			buffer.putInt(key.getAuthAccountId());
			buffer.putInt(key.getAuthKey());
		}
	}

	@Override
	protected int size(ServerClient client) {
		return super.size(client) + 16 + 2 * account.length();
	}
}