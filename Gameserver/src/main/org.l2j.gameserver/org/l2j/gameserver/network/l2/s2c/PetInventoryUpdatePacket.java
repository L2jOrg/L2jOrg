package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.model.items.ItemInfo;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.GameClient;

public class PetInventoryUpdatePacket extends L2GameServerPacket
{
	public static final int UNCHANGED = 0;
	public static final int ADDED = 1;
	public static final int MODIFIED = 2;
	public static final int REMOVED = 3;

	private final List<ItemInfo> _items = new ArrayList<ItemInfo>(1);

	public PetInventoryUpdatePacket()
	{}

	public PetInventoryUpdatePacket addNewItem(ItemInstance item)
	{
		addItem(item).setLastChange(ADDED);
		return this;
	}

	public PetInventoryUpdatePacket addModifiedItem(ItemInstance item)
	{
		addItem(item).setLastChange(MODIFIED);
		return this;
	}

	public PetInventoryUpdatePacket addRemovedItem(ItemInstance item)
	{
		addItem(item).setLastChange(REMOVED);
		return this;
	}

	private ItemInfo addItem(ItemInstance item)
	{
		ItemInfo info;
		_items.add(info = new ItemInfo(item));
		return info;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putShort((short) _items.size());
		for(ItemInfo temp : _items)
		{
			buffer.putShort((short) temp.getLastChange());
			writeItemInfo(buffer, temp);
		}
	}
}