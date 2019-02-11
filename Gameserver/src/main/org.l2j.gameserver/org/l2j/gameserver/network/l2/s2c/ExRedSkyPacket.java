package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExRedSkyPacket extends L2GameServerPacket
{
	private int _duration;

	public ExRedSkyPacket(int duration)
	{
		_duration = duration;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_duration);
	}
}