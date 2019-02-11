package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_itemObjId);
		buffer.putLong(_itemCount);
		writeString(_sellerName, buffer);
	}
}