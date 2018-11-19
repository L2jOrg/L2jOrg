package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Creature;

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
		writeD(_charId);
		writeD(_degree);
		writeD(_speed);
		writeD(0x00); //??
	}
}