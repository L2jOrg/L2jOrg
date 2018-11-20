package org.l2j.authserver.network.gameserver.packet.auth2game;

import org.l2j.authserver.AuthServer;

public class Protocol extends GameServerWritablePacket {

	@Override
	protected void writeImpl()  {
		writeByte(0x00);
		writeInt(AuthServer.PROTOCOL_REV);
		var publickey = client.getPublicKey().getModulus().toByteArray();
		writeInt(publickey.length);
		writeBytes(publickey);
	}

	@Override
	protected int packetSize() {
	    return super.packetSize() + 9 + client.getPublicKey().getModulus().toByteArray().length;
	}
}
