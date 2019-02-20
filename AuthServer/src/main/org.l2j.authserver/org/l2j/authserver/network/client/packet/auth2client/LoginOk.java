package org.l2j.authserver.network.client.packet.auth2client;

import org.l2j.authserver.network.client.AuthClient;
import org.l2j.authserver.network.client.packet.L2LoginServerPacket;

import java.nio.ByteBuffer;

/**
 * Format: dddddddd f: the session key d: ? d: ? d: ? d: ? d: ? d: ? b: 16 bytes - unknown
 */
public final class LoginOk extends L2LoginServerPacket {

	@Override
	protected void writeImpl(AuthClient client, ByteBuffer buffer) {
		var sessionKey = client.getSessionKey();
		buffer.put((byte)0x03);
		buffer.putInt(sessionKey.getAuthAccountId());
		buffer.putInt(sessionKey.getAuthKey());
		buffer.put(new byte[8]);
		buffer.putInt(0x000003ea); // billing type: 1002 Free, x200 paid time, x500 flat rate pre paid, others subscription
		buffer.putInt(0x00); // paid time
		buffer.putInt(0x00);
		buffer.putInt(0x00); // warning mask
		buffer.put(new byte[16]); // forbidden servers
		buffer.putInt(0x00);
	}

	@Override
	protected int size(AuthClient client) {
		return super.size(client) + 53;
	}
}
