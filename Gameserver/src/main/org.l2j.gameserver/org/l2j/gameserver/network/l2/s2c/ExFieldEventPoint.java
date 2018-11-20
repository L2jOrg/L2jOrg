package org.l2j.gameserver.network.l2.s2c;

public class ExFieldEventPoint extends L2GameServerPacket
{
	private final int _points;

	public ExFieldEventPoint(int points)
	{
		_points = points;
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_points);
	}
}