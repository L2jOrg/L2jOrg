package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * 15
 * ee cc 11 43 		object id
 * 39 00 00 00 		item id
 * 8f 14 00 00 		x
 * b7 f1 00 00 		y
 * 60 f2 ff ff 		z
 * 01 00 00 00 		show item count
 * 7a 00 00 00      count                                         .
 *
 * format  dddddddd
 */
public class SpawnItemPacket extends L2GameServerPacket
{
	private int _objectId;
	private int _itemId;
	private int _x, _y, _z;
	private int _stackable;
	private long _count;
	private final int _enchantLevel;
	private final int _ensoulCount;

	public SpawnItemPacket(ItemInstance item)
	{
		_objectId = item.getObjectId();
		_itemId = item.getItemId();
		_x = item.getX();
		_y = item.getY();
		_z = item.getZ();
		_stackable = item.isStackable() ? 0x01 : 0x00;
		_count = item.getCount();
		_enchantLevel = item.getEnchantLevel();
		_ensoulCount = item.getNormalEnsouls().length + item.getSpecialEnsouls().length;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_objectId);
		buffer.putInt(_itemId);

		buffer.putInt(_x);
		buffer.putInt(_y);
		buffer.putInt(_z + Config.CLIENT_Z_SHIFT);
		buffer.putInt(_stackable);
		buffer.putLong(_count);
		buffer.putInt(0x00); //c2
		buffer.put((byte)_enchantLevel);
		buffer.put((byte)0x00);
		buffer.put((byte)_ensoulCount);
	}
}