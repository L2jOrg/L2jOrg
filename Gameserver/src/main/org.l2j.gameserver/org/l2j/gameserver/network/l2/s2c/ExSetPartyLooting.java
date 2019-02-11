package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExSetPartyLooting extends L2GameServerPacket
{
	private int _result;
	private int _mode;

	public ExSetPartyLooting(int result, int mode)
	{
		_result = result;
		_mode = mode;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_result);
		buffer.putInt(_mode);
	}
}
