package l2s.gameserver.network.l2.s2c;

public class ExPutEnchantSupportItemResult extends L2GameServerPacket
{
	public static final L2GameServerPacket FAIL = new ExPutEnchantSupportItemResult(0x01);
	public static final L2GameServerPacket SUCCESS = new ExPutEnchantSupportItemResult(0x01);

	private int _result;

	public ExPutEnchantSupportItemResult(int result)
	{
		_result = result;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_result);
	}
}