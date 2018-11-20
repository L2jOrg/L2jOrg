package org.l2j.authserver.network.gameserver.packet.auth2game;

public class PlayerAuthResponse extends GameServerWritablePacket {

	private final String account;
	private final int response;

	public PlayerAuthResponse(String account, int response) {
		this.account = account;
		this.response = response;
	}

	@Override
	protected void writeImpl()  {
		writeByte(0x03);
		writeString(account);
		writeByte(response);
	}

	@Override
	protected int packetSize() {
		return super.packetSize() + 4 + 2 * account.length();
	}
}