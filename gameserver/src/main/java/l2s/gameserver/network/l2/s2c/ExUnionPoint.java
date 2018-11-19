package l2s.gameserver.network.l2.s2c;

/**
 * @author Bonux
**/
public class ExUnionPoint extends L2GameServerPacket
{
	private final int _clanId;

	public ExUnionPoint(int clanId)
	{
		_clanId = clanId;

	}

	@Override
	protected final void writeImpl()
	{
		writeD(_clanId);
	}
}
