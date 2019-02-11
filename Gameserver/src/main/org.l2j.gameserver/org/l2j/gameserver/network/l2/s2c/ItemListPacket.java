package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.model.items.LockType;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ItemListPacket extends L2GameServerPacket
{
	private final int _size;
	private final ItemInstance[] _items;
	private final boolean _showWindow;

	private LockType _lockType;
	private int[] _lockItems;

	private Player _player;

	public ItemListPacket(Player player, int size, ItemInstance[] items, boolean showWindow, LockType lockType, int[] lockItems)
	{
		_player = player;
		_size = size;
		_items = items;
		_showWindow = showWindow;
		_lockType = lockType;
		_lockItems = lockItems;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putShort((short) (_showWindow ? 1 : 0));

		buffer.putShort((short) _size);
		for(ItemInstance temp : _items)
		{
			if(temp.getTemplate().isQuest())
				continue;

			writeItemInfo(buffer, _player, temp);
		}

		buffer.putShort((short) _lockItems.length);
		if(_lockItems.length > 0)
		{
			buffer.put((byte)_lockType.ordinal());
			for(int i : _lockItems)
				buffer.putInt(i);
		}
	}
}