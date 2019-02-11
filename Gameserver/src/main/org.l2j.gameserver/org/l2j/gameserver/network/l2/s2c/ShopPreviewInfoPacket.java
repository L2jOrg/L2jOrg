package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.Map;

import org.l2j.gameserver.model.items.Inventory;
import org.l2j.gameserver.network.l2.GameClient;

public class ShopPreviewInfoPacket extends L2GameServerPacket
{
	private Map<Integer, Integer> _itemlist;

	public ShopPreviewInfoPacket(Map<Integer, Integer> itemlist)
	{
		_itemlist = itemlist;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(Inventory.PAPERDOLL_MAX);

		// Slots
		for(int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER)
			buffer.putInt(getFromList(PAPERDOLL_ID));
	}

	private int getFromList(int key)
	{
		return ((_itemlist.get(key) != null) ? _itemlist.get(key) : 0);
	}
}