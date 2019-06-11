package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

import java.nio.ByteBuffer;

public class PlayerAuthRequest extends SendablePacket
{
	private String account;
	private int playOkID1, playOkID2, loginOkID1, loginOkID2;

	public PlayerAuthRequest(L2GameClient client)
	{
		account = client.getAccountName();
		playOkID1 = client.getSessionId().getGameServerSessionId();
		playOkID2 = client.getSessionId().getGameServerAccountId();
		loginOkID1 = client.getSessionId().getAuthAccountId();
		loginOkID2 = client.getSessionId().getAuthKey();
	}

	protected void writeImpl(AuthServerClient client) {
		writeByte((byte)0x02);
		writeString(account);
		writeInt(playOkID1);
		writeInt(playOkID2);
		writeInt(loginOkID1);
		writeInt(loginOkID2);
	}
}