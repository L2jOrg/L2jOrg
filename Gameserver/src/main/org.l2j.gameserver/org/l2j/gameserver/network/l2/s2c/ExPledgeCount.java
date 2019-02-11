package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * @author Bonux
**/
public class ExPledgeCount extends L2GameServerPacket
{
	private final int _count;

	public ExPledgeCount(int count)
	{
		_count = count;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_count);
	}
}