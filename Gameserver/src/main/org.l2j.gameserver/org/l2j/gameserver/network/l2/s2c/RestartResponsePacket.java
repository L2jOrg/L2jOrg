package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;

@StaticPacket
public class RestartResponsePacket extends L2GameServerPacket  {
	public static final RestartResponsePacket OK = new RestartResponsePacket(1), FAIL = new RestartResponsePacket(0);
	private String _message;
	private int _param;

	private RestartResponsePacket(int param) {
		_message = "bye";
		_param = param;
	}

	@Override
	protected final void writeImpl() {
		writeInt(_param); //01-ok
		writeString(_message);
	}

	@Override
	protected int packetSize() {
		return 7 + _message.length() * 2;
	}
}