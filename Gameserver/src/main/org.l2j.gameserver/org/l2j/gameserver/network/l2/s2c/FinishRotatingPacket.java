package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Creature;

public class FinishRotatingPacket extends L2GameServerPacket
{
	private int _charId, _degree, _speed;

	public FinishRotatingPacket(Creature player, int degree, int speed)
	{
		_charId = player.getObjectId();
		_degree = degree;
		_speed = speed;
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_charId);
		writeInt(_degree);
		writeInt(_speed);
		writeInt(0x00); //??
	}
}