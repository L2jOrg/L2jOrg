package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.instances.PetInstance;
import org.l2j.gameserver.model.items.ItemInstance;

public class PetItemListPacket extends L2GameServerPacket
{
	private ItemInstance[] items;

	public PetItemListPacket(PetInstance cha)
	{
		items = cha.getInventory().getItems();
	}

	@Override
	protected final void writeImpl()
	{
		writeH(items.length);

		for(ItemInstance item : items)
			writeItemInfo(item);
	}
}