package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_charId);
		buffer.putInt(_degree);
		buffer.putInt(_speed);
		buffer.putInt(0x00); //??
	}
}