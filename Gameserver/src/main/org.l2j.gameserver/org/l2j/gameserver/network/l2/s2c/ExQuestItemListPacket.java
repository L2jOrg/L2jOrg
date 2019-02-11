package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.model.items.LockType;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * @author VISTALL
 * @date 1:02/23.02.2011
 */
public class ExQuestItemListPacket extends L2GameServerPacket
{
	private int _size;
	private ItemInstance[] _items;

	private LockType _lockType;
	private int[] _lockItems;

	public ExQuestItemListPacket(int size, ItemInstance[] t, LockType lockType, int[] lockItems)
	{
		_size = size;
		_items = t;
		_lockType = lockType;
		_lockItems = lockItems;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putShort((short) _size);

		for(ItemInstance temp : _items)
		{
			if(!temp.getTemplate().isQuest())
				continue;

			writeItemInfo(buffer, temp);
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
