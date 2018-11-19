package l2s.gameserver.network.l2.s2c;

public class TradeDonePacket extends L2GameServerPacket
{
	public static final L2GameServerPacket SUCCESS = new TradeDonePacket(1);
	public static final L2GameServerPacket FAIL = new TradeDonePacket(0);

	private int _response;

	private TradeDonePacket(int num)
	{
		_response = num;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_response);
	}
}