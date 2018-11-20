package org.l2j.gameserver.network.l2.s2c;

public class ExPrivateStoreSellingResult extends L2GameServerPacket
{
	private final int _itemObjId;
	private final long _itemCount;
	private final String _buyerName;

	public ExPrivateStoreSellingResult(int itemObjId, long itemCount, String buyerName)
	{
		_itemObjId = itemObjId;
		_itemCount = itemCount;
		_buyerName = buyerName;
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_itemObjId);
		writeLong(_itemCount);
		writeString(_buyerName);
	}
}