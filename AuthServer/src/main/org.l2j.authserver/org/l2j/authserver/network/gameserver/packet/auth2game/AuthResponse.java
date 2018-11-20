package org.l2j.authserver.network.gameserver.packet.auth2game;

import org.l2j.authserver.controller.GameServerManager;

public class AuthResponse extends GameServerWritablePacket {

	private final int serverId;
	private final String serverName;

	public AuthResponse(int serverId) {
		this.serverId = serverId;
        serverName = GameServerManager.getInstance().getServerNameById(serverId);
	}

	@Override
	protected void writeImpl() {
		writeByte(0x02);
		writeByte(serverId);
		writeString(serverName);
	}

    @Override
    protected int packetSize() {
        return super.packetSize() + 4 + 2 * serverName.length();
    }
}
