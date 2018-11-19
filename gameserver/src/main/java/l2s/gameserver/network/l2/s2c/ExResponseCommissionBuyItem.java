package l2s.gameserver.network.l2.s2c;

/**
 * @author : Ragnarok & Bonux
 * @date : 22.04.12  12:09
 */
public class ExResponseCommissionBuyItem extends L2GameServerPacket
{
	public static final ExResponseCommissionBuyItem FAILED = new ExResponseCommissionBuyItem();

	private int _code;
	private int _itemId;
	private long _count;

	public ExResponseCommissionBuyItem()
	{
		_code = 0;
	}

	public ExResponseCommissionBuyItem(int itemId, long count)
	{
		_code = 1;
		_itemId = itemId;
		_count = count;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_code);
		if(_code == 0)
			return;

		writeD(0x00); //unk, maybe item object Id
		writeD(_itemId);
		writeQ(_count);
	}
}
