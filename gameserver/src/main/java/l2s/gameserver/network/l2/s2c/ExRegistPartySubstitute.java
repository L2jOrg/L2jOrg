package l2s.gameserver.network.l2.s2c;

public class ExRegistPartySubstitute extends L2GameServerPacket
{
	private final int _object;

	public ExRegistPartySubstitute(int obj)
	{
		_object = obj;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_object);
		writeD(0x01);
	}
}
