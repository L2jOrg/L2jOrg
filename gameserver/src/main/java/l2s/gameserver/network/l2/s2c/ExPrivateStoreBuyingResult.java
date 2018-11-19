package l2s.gameserver.network.l2.s2c;

public class ExPrivateStoreBuyingResult extends L2GameServerPacket
{
	private final int _itemObjId;
	private final long _itemCount;
	private final String _sellerName;

	public ExPrivateStoreBuyingResult(int itemObjId, long itemCount, String sellerName)
	{
		_itemObjId = itemObjId;
		_itemCount = itemCount;
		_sellerName = sellerName;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_itemObjId);
		writeQ(_itemCount);
		writeS(_sellerName);
	}
}