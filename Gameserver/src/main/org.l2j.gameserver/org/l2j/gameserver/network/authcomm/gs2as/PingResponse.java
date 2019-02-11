package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

import java.nio.ByteBuffer;

public class PingResponse extends SendablePacket
{
	protected void writeImpl(AuthServerClient client, ByteBuffer buffer) {
		buffer.put((byte)0xff);
		buffer.putLong(System.currentTimeMillis());
	}
}