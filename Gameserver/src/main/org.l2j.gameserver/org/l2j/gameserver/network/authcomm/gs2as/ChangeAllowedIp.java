package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

import java.nio.ByteBuffer;

public class ChangeAllowedIp extends SendablePacket
{
	private String account;
	private String ip;

	public ChangeAllowedIp(String account, String ip)
	{
		this.account = account;
		this.ip = ip;
	}

	@Override
	protected void writeImpl(AuthServerClient client, ByteBuffer buffer) {
		buffer.put((byte)0x07);
		writeString(account, buffer);
		writeString(ip, buffer);
	}
}