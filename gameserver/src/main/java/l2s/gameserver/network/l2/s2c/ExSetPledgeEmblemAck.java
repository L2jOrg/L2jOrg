package l2s.gameserver.network.l2.s2c;

/**
 * @author Bonux
**/
public class ExSetPledgeEmblemAck extends L2GameServerPacket
{
	private final int _part;

	public ExSetPledgeEmblemAck(int part)
	{
		_part = part;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_part);
	}
}