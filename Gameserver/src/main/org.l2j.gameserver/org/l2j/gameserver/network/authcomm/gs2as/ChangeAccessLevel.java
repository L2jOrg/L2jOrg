package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

import java.nio.ByteBuffer;

public class ChangeAccessLevel extends SendablePacket
{
	private String account;
	private int level;
	private int banExpire;

	public ChangeAccessLevel(String account, int level, int banExpire)
	{
		this.account = account;
		this.level = level;
		this.banExpire = banExpire;
	}

	protected void writeImpl(AuthServerClient client, ByteBuffer buffer) {
		buffer.put((byte)0x11);
		writeString(account, buffer);
		buffer.putInt(level);
		buffer.putInt(banExpire);
	}
}
