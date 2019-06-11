package org.l2j.authserver.network.gameserver.packet.auth2game;

import org.l2j.authserver.controller.GameServerManager;
import org.l2j.authserver.network.gameserver.ServerClient;

import java.nio.ByteBuffer;

public class AuthResponse extends GameServerWritablePacket {

	private final int serverId;
	private final String serverName;

	public AuthResponse(int serverId) {
		this.serverId = serverId;
        serverName = GameServerManager.getInstance().getServerNameById(serverId);
	}

	@Override
	protected void writeImpl(ServerClient client) {
		writeByte((byte)0x00);
		writeByte((byte)serverId);
		writeString(serverName);
	}

}
