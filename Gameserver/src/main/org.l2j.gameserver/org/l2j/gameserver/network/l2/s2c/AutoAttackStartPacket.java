package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class AutoAttackStartPacket extends L2GameServerPacket
{
	// dh
	private int _targetId;

	public AutoAttackStartPacket(int targetId)
	{
		_targetId = targetId;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_targetId);
	}
}