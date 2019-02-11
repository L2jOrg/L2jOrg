package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.instances.PetInstance;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class PetItemListPacket extends L2GameServerPacket
{
	private ItemInstance[] items;

	public PetItemListPacket(PetInstance cha)
	{
		items = cha.getInventory().getItems();
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putShort((short) items.length);

		for(ItemInstance item : items)
			writeItemInfo(buffer, item);
	}
}