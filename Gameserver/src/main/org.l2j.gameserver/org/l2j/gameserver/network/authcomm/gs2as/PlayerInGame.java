package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.SendablePacket;

public class PlayerInGame extends SendablePacket
{
	private String account;

	public PlayerInGame(String account)
	{
		this.account = account;
	}

	@Override
	protected void writeImpl()
	{
		writeByte(0x03);
		writeString(account);
	}
}
