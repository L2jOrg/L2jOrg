package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer) {
		buffer.putInt(_param); //01-ok
		writeString(_message, buffer);
	}

	@Override
	protected int size(GameClient client) {
		return 7 + _message.length() * 2;
	}
}