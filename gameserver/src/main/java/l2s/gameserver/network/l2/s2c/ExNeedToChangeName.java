package l2s.gameserver.network.l2.s2c;

public class ExNeedToChangeName extends L2GameServerPacket
{
	private int _type, _reason;
	private String _origName;

	public ExNeedToChangeName(int type, int reason, String origName)
	{
		_type = type;
		_reason = reason;
		_origName = origName;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_type);
		writeD(_reason);
		writeS(_origName);
	}
}