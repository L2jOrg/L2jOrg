package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExEnterWorldPacket extends L2GameServerPacket
{
	private final int _serverTime;

	public ExEnterWorldPacket()
	{
		_serverTime = (int) (System.currentTimeMillis() / 1000L);
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_serverTime);
	}

	@Override
	protected int size(GameClient client) {
		return 9;
	}
}