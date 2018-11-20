package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.items.ItemInfo;

/**
 * @author Bonux
 */
public class ExChangeAttributeItemList extends L2GameServerPacket
{
	private ItemInfo[] _itemsList;
	private int _itemId;

	public ExChangeAttributeItemList(int itemId, ItemInfo[] itemsList)
	{
		_itemId = itemId;
		_itemsList = itemsList;
	}

	protected void writeImpl()
	{
		writeD(_itemId);
		writeD(_itemsList.length); //size
		for(ItemInfo item : _itemsList)
		{
			writeItemInfo(item);
		}
	}
}