package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Creature;

public class StartRotatingPacket extends L2GameServerPacket
{
	private int _charId, _degree, _side, _speed;

	public StartRotatingPacket(Creature cha, int degree, int side, int speed)
	{
		_charId = cha.getObjectId();
		_degree = degree;
		_side = side;
		_speed = speed;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_charId);
		writeD(_degree);
		writeD(_side);
		writeD(_speed);
	}
}