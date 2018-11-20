package org.l2j.authserver.network.client.packet.auth2client;

import org.l2j.authserver.network.client.packet.L2LoginServerPacket;

/**
 * Format: dddddddd f: the session key d: ? d: ? d: ? d: ? d: ? d: ? b: 16 bytes - unknown
 */
public final class LoginOk extends L2LoginServerPacket {

	@Override
	protected void write() {
		var sessionKey = client.getSessionKey();
		writeByte(0x03);
		writeInt(sessionKey.authAccountId);
		writeInt(sessionKey.authKey);
		writeBytes(new byte[8]);
		writeInt(0x000003ea); // billing type: 1002 Free, x200 paid time, x500 flat rate pre paid, others subscription
		writeInt(0x00); // paid time
		writeInt(0x00);
		writeInt(0x00); // warning mask
		writeBytes(new byte[16]); // forbidden servers
		writeInt(0x00);
	}

	@Override
	protected int packetSize() {
		return super.packetSize() + 53;
	}
}
