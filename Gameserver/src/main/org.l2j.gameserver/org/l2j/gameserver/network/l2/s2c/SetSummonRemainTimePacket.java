package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Servitor;

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
	protected final void writeImpl()
	{
		writeInt(_maxFed);
		writeInt(_curFed);
	}
}