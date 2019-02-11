package org.l2j.authserver.network.gameserver.packet.auth2game;

import org.l2j.authserver.AuthServer;
import org.l2j.authserver.network.gameserver.ServerClient;

import java.nio.ByteBuffer;

public class Protocol extends GameServerWritablePacket {

	@Override
	protected void writeImpl(ServerClient client, ByteBuffer buffer)  {
		buffer.put((byte)0x00);
		buffer.putInt(AuthServer.PROTOCOL_REV);
		var publickey = client.getPublicKey().getModulus().toByteArray();
		buffer.putInt(publickey.length);
		buffer.put(publickey);
	}

	@Override
	protected int size(ServerClient client) {
	    return super.size(client) + 9 + client.getPublicKey().getModulus().toByteArray().length;
	}
}
