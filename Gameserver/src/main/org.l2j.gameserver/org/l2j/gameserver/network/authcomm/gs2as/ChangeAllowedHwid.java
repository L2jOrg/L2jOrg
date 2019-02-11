package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

import java.nio.ByteBuffer;

public class ChangeAllowedHwid extends SendablePacket
{
	private String account;
	private String hwid;

	public ChangeAllowedHwid(String account, String hwid)
	{
		this.account = account;
		this.hwid = hwid;
	}

	@Override
	protected void writeImpl(AuthServerClient client, ByteBuffer buffer) {
		buffer.put((byte)0x09);
		writeString(account, buffer);
		writeString(hwid, buffer);
	}
}