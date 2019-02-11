package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_itemObjId);
		buffer.putLong(_itemCount);
		writeString(_buyerName, buffer);
	}
}