package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExFieldEventStep extends L2GameServerPacket
{
	private final int _own;
	private final int _cumulative;
	private final int _max;

	public ExFieldEventStep(int own, int cumulative, int max)
	{
		_own = own;
		_cumulative = cumulative;
		_max = max;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_own);
		buffer.putInt(_cumulative);
		buffer.putInt(_max);
	}
}