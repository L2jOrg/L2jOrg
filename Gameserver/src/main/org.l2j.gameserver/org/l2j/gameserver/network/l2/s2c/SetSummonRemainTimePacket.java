package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Servitor;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class SetSummonRemainTimePacket extends L2GameServerPacket
{
	private final int _maxFed;
	private final int _curFed;

	public SetSummonRemainTimePacket(Servitor summon)
	{
		_curFed = summon.getCurrentFed();
		_maxFed = summon.getMaxFed();
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_maxFed);
		buffer.putInt(_curFed);
	}
}