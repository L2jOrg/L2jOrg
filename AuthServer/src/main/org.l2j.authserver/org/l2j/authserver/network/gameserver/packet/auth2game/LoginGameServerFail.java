package org.l2j.authserver.network.gameserver.packet.auth2game;

public class LoginGameServerFail extends GameServerWritablePacket {

	public static final int REASON_IP_BANNED = 1;
	public static final int REASON_IP_RESERVED = 2;
	public static final int REASON_ID_RESERVED = 4;
	public static final int REASON_NO_FREE_ID = 5;
	public static final int NOT_AUTHED = 6;
	public static final int REASON_ALREADY_LOGGED = 7;

	private final int reason;

	public LoginGameServerFail(int reason) {
		this.reason = reason;
	}

	@Override
	protected void writeImpl() {
		writeByte(0x01);
		writeByte(reason);
	}

	@Override
	protected int packetSize() {
		return super.packetSize() + 2;
	}
}
