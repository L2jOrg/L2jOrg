package l2s.gameserver.network.l2.s2c;

public class ExFieldEventEffect extends L2GameServerPacket
{
	private final int _unk;

	public ExFieldEventEffect(int unk)
	{
		_unk = unk;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_unk);
	}
}