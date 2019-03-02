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
	private final int sendType;

	private LockType _lockType;
	private int[] _lockItems;

	private Player _player;

	public ItemListPacket(int sendType, Player player, int size, ItemInstance[] items, boolean showWindow, LockType lockType, int[] lockItems) {
		this.sendType = sendType;
		_player = player;
		_size = size;
		_items = items;
		_showWindow = showWindow;
		_lockType = lockType;
		_lockItems = lockItems;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer) {

		if(sendType == 2) {
			buffer.put((byte) sendType);
			buffer.putInt(_size);
			buffer.putInt(_size);

			for(ItemInstance temp : _items)
			{
				if(temp.getTemplate().isQuest())
					continue;

				writeItemInfo(buffer, _player, temp);
			}
		} else {
			buffer.put((byte) 0x01); // show windown
			buffer.putInt(0);
			buffer.putInt(_size);
		}

		if(_lockItems.length > 0)
		{
			buffer.put((byte)_lockType.ordinal());
			for(int i : _lockItems)
				buffer.putInt(i);
		}
	}
}