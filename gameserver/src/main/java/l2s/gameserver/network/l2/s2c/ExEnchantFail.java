package l2s.gameserver.network.l2.s2c;

/**
 * @author Bonux
**/
public final class ExEnchantFail extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC = new ExEnchantFail(0, 0);

	private final int _itemOne;
	private final int _itemTwo;

	public ExEnchantFail(int itemOne, int itemTwo)
	{
		_itemOne = itemOne;
		_itemTwo = itemTwo;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_itemOne);
		writeD(_itemTwo);
	}
}