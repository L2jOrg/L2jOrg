package l2s.gameserver.network.l2.s2c;

public class ExFieldEventStep extends L2GameServerPacket
{
	private final int _own;
	private final int _cumulative;
	private final int _max;

	public ExFieldEventStep(int own, int cumulative, int max)
	{
		_own = own;
		_cumulative = cumulative;
		_max = max;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_own);
		writeD(_cumulative);
		writeD(_max);
	}
}