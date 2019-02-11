package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExShowQuestMarkPacket extends L2GameServerPacket
{
	private final int _questId, _cond;

	public ExShowQuestMarkPacket(int questId, int cond)
	{
		_questId = questId;
		_cond = cond;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_questId);
		buffer.putInt(_cond);
	}
}