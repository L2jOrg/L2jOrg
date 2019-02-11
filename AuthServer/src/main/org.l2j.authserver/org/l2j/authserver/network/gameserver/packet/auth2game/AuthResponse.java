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
	protected void writeImpl(ServerClient client, ByteBuffer buffer) {
		buffer.put((byte)0x00);
		buffer.put((byte)serverId);
		writeString(serverName, buffer);
	}

    @Override
    protected int size(ServerClient client) {
        return super.size(client) + 4 + 2 * serverName.length();
    }
}
