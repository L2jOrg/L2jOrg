package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.items.ItemInstance;

public class ExResponseCommissionInfo extends L2GameServerPacket
{
	private ItemInstance _item;

	public ExResponseCommissionInfo(ItemInstance item)
	{
		_item = item;
	}

	protected void writeImpl()
	{
		writeInt(_item.getItemId()); //ItemId
		writeInt(_item.getObjectId());
		writeLong(_item.getCount()); //TODO
		writeLong(0/*_item.getCount()*/); //TODO
		writeInt(0); //TODO
	}
}
