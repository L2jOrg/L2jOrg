package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInfo;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.GameClient;

public class InventoryUpdatePacket extends L2GameServerPacket
{
	public static final int UNCHANGED = 0;
	public static final int ADDED = 1;
	public static final int MODIFIED = 2;
	public static final int REMOVED = 3;

	private final List<ItemInfo> _items = new ArrayList<ItemInfo>(1);

	public InventoryUpdatePacket addNewItem(Player player, ItemInstance item)
	{
		addItem(player, item).setLastChange(ADDED);
		return this;
	}

	public InventoryUpdatePacket addModifiedItem(Player player, ItemInstance item)
	{
		addItem(player, item).setLastChange(MODIFIED);
		return this;
	}

	public InventoryUpdatePacket addRemovedItem(Player player, ItemInstance item)
	{
		addItem(player, item).setLastChange(REMOVED);
		return this;
	}

	private ItemInfo addItem(Player player, ItemInstance item)
	{
		ItemInfo info;
		_items.add(info = new ItemInfo(item, item.getTemplate().isBlocked(player, item)));
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