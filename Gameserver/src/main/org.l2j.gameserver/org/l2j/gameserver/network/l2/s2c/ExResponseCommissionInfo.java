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
		writeD(_item.getItemId()); //ItemId
		writeD(_item.getObjectId());
		writeQ(_item.getCount()); //TODO
		writeQ(0/*_item.getCount()*/); //TODO
		writeD(0); //TODO
	}
}
