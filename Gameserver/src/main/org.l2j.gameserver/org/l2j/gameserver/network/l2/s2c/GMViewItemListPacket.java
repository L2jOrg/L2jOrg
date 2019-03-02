package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class GMViewItemListPacket extends L2GameServerPacket
{
	private final int sendType;
	private int _size;
	private ItemInstance[] _items;
	private int _limit;
	private String _name;
	private Player _player;

	public GMViewItemListPacket(int sendType, Player cha, ItemInstance[] items, int size) {
		this.sendType = sendType;
		_size = size;
		_items = items;
		_name = cha.getName();
		_limit = cha.getInventoryLimit();
		_player = cha;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.put((byte) sendType);
		if(sendType == 2) {
			buffer.putInt(_size);
		} else {
			writeString(_name, buffer);
			buffer.putInt(_limit); //c4?
		}

		buffer.putInt(_size);
		for(ItemInstance temp : _items)
		{
			if(!temp.getTemplate().isQuest())
				writeItemInfo(buffer, _player, temp);
		}
	}
}