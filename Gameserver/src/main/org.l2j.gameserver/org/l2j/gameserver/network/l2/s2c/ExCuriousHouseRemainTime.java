package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExCuriousHouseRemainTime extends L2GameServerPacket
{
	private int _time;

	public ExCuriousHouseRemainTime(int time)
	{
		_time = time;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_time);
	}
}
