package l2s.gameserver.network.l2.s2c;

public class ExPutEnchantTargetItemResult extends L2GameServerPacket
{
	public static final L2GameServerPacket FAIL = new ExPutEnchantTargetItemResult(0);
	public static final L2GameServerPacket SUCCESS = new ExPutEnchantTargetItemResult(1);

	private int _result;

	public ExPutEnchantTargetItemResult(int result)
	{
		_result = result;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_result);
	}
}