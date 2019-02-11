package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class JoinPledgePacket extends L2GameServerPacket
{
	private int _pledgeId;

	public JoinPledgePacket(int pledgeId)
	{
		_pledgeId = pledgeId;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_pledgeId);
	}
}