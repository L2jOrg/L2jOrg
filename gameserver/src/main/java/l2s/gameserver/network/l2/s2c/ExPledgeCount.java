package l2s.gameserver.network.l2.s2c;

/**
 * @author Bonux
**/
public class ExPledgeCount extends L2GameServerPacket
{
	private final int _count;

	public ExPledgeCount(int count)
	{
		_count = count;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_count);
	}
}