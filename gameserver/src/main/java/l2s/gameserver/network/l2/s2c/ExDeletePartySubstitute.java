package l2s.gameserver.network.l2.s2c;

/**
 *
 * @author monithly
 */
public class ExDeletePartySubstitute extends L2GameServerPacket
{
	private final int _obj;

	public ExDeletePartySubstitute(final int objectId)
	{
		_obj = objectId;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_obj);
	}
}
