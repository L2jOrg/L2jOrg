package l2s.gameserver.network.l2.s2c;

public class ExRotation extends L2GameServerPacket
{
	private int _charObjId, _degree;

	public ExRotation(int charId, int degree)
	{
		_charObjId = charId;
		_degree = degree;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_charObjId);
		writeD(_degree);
	}
}
