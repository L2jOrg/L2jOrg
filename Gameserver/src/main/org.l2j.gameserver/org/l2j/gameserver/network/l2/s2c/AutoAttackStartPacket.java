package org.l2j.gameserver.network.l2.s2c;

public class AutoAttackStartPacket extends L2GameServerPacket
{
	// dh
	private int _targetId;

	public AutoAttackStartPacket(int targetId)
	{
		_targetId = targetId;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_targetId);
	}
}