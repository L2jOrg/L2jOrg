package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class GMViewItemListPacket extends L2GameServerPacket
{
	private int _size;
	private ItemInstance[] _items;
	private int _limit;
	private String _name;
	private Player _player;

	public GMViewItemListPacket(Player cha, ItemInstance[] items, int size)
	{
		_size = size;
		_items = items;
		_name = cha.getName();
		_limit = cha.getInventoryLimit();
		_player = cha;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		writeString(_name, buffer);
		buffer.putInt(_limit); //c4?
		buffer.putShort((short) 1); // show window ??

		buffer.putShort((short) _size);
		for(ItemInstance temp : _items)
		{
			if(!temp.getTemplate().isQuest())
				writeItemInfo(buffer, _player, temp);
		}
	}
}