package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

import java.nio.ByteBuffer;

public class ReduceAccountPoints extends SendablePacket
{
	private String account;
	private int count;

	public ReduceAccountPoints(String account, int count)
	{
		this.account = account;
		this.count = count;
	}

	protected void writeImpl(AuthServerClient client, ByteBuffer buffer) {
		buffer.put((byte)0x12);
		writeString(account, buffer);
		buffer.putInt(count);
	}
}