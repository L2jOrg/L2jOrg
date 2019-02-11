package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExResponseCommissionInfo extends L2GameServerPacket
{
	private ItemInstance _item;

	public ExResponseCommissionInfo(ItemInstance item)
	{
		_item = item;
	}

	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_item.getItemId()); //ItemId
		buffer.putInt(_item.getObjectId());
		buffer.putLong(_item.getCount()); //TODO
		buffer.putLong(0/*_item.getCount()*/); //TODO
		buffer.putInt(0); //TODO
	}
}
