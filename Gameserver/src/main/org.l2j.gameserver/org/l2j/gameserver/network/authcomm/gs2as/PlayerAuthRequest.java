package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class PlayerAuthRequest extends SendablePacket
{
	private String account;
	private int playOkID1, playOkID2, loginOkID1, loginOkID2;

	public PlayerAuthRequest(GameClient client)
	{
		account = client.getLogin();
		playOkID1 = client.getSessionKey().gameserverSession;
		playOkID2 = client.getSessionKey().gameserverAccountId;
		loginOkID1 = client.getSessionKey().authAccountId;
		loginOkID2 = client.getSessionKey().authKey;
	}

	protected void writeImpl(AuthServerClient client, ByteBuffer buffer) {
		buffer.put((byte)0x02);
		writeString(account, buffer);
		buffer.putInt(playOkID1);
		buffer.putInt(playOkID2);
		buffer.putInt(loginOkID1);
		buffer.putInt(loginOkID2);
	}
}