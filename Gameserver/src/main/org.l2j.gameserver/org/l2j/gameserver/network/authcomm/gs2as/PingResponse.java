package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

public class PingResponse extends SendablePacket {

	protected void writeImpl(AuthServerClient client) {
		writeByte((byte)0xff);
		writeLong(System.currentTimeMillis());
	}
}