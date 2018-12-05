package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.SendablePacket;
import org.l2j.gameserver.network.l2.GameClient;

public class PlayerAuthRequest extends SendablePacket
{
	private String account;
	private int playOkID1, playOkID2, loginOkID1, loginOkID2;

	public PlayerAuthRequest(GameClient client)
	{
		account = client.getLogin();
		playOkID1 = client.getSessionKey().playOkID1;
		playOkID2 = client.getSessionKey().playOkID2;
		loginOkID1 = client.getSessionKey().loginOkID1;
		loginOkID2 = client.getSessionKey().loginOkID2;
	}

	protected void writeImpl()
	{
		writeByte(0x02);
		writeString(account);
		writeInt(playOkID1);
		writeInt(playOkID2);
		writeInt(loginOkID1);
		writeInt(loginOkID2);
	}
}