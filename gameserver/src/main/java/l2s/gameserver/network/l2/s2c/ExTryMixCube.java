package l2s.gameserver.network.l2.s2c;

/**
 * @author Bonux
**/
public class ExTryMixCube extends L2GameServerPacket
{
	public static final L2GameServerPacket FAIL = new ExTryMixCube(6);

	private final int _result;
	private final int _itemId;
	private final long _itemCount;

	public ExTryMixCube(int result)
	{
		_result = result;
		_itemId = 0;
		_itemCount = 0;
	}

	public ExTryMixCube(int itemId, long itemCount)
	{
		_result = 0;
		_itemId = itemId;
		_itemCount = itemCount;
	}

	@Override
	protected void writeImpl()
	{
		writeC(_result);
		writeD(0x01); // UNK
		//for(int i = 0; i < count; i++)
		//{
			writeC(0x00);
			writeD(_itemId);
			writeQ(_itemCount);
		//}
	}
}