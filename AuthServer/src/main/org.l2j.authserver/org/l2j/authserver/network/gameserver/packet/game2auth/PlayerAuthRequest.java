package org.l2j.authserver.network.gameserver.packet.game2auth;

import org.l2j.authserver.controller.AuthController;
import org.l2j.authserver.network.SessionKey;
import org.l2j.authserver.network.gameserver.packet.auth2game.PlayerAuthResponse;

import java.nio.ByteBuffer;
import java.util.Objects;

public class PlayerAuthRequest extends GameserverReadablePacket {
	
	private  String account;
	private int sessionId;
	private int serverAccountId;
	private int authAccountId;
	private int authKey;

	@Override
	protected void readImpl(ByteBuffer buffer) {
		account = readString(buffer);
		sessionId = buffer.getInt();
		serverAccountId = buffer.getInt();
		authAccountId = buffer.getInt();
		authKey = buffer.getInt();
	}

	@Override
	protected void runImpl()  {
		var sessionKey = new SessionKey(authAccountId, authKey, sessionId, serverAccountId);
		var authedkey = AuthController.getInstance().getKeyForAccount(account);

		PlayerAuthResponse authResponse;
		if(Objects.equals(sessionKey, authedkey)) {
			AuthController.getInstance().removeAuthedClient(account);
			authResponse = new PlayerAuthResponse(account, 1);
		} else {
			authResponse = new PlayerAuthResponse(account, 0);
		}

		client.sendPacket(authResponse);
	}
}