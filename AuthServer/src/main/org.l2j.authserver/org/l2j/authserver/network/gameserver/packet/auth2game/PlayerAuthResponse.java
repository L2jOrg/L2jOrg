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
	protected void writeImpl(ServerClient client)  {
		writeByte((byte)0x02);
		writeString(account);
		writeByte((byte) (response ? 0x01 : 0x00));
		if(response) {
			var key  = AuthController.getInstance().getKeyForAccount(account);
			writeInt(key.getGameServerSessionId());
			writeInt(key.getGameServerAccountId());
			writeInt(key.getAuthAccountId());
			writeInt(key.getAuthKey());
		}
	}

}