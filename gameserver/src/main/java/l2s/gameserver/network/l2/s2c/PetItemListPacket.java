package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.instances.PetInstance;
import l2s.gameserver.model.items.ItemInstance;

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