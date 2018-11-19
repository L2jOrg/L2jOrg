package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.Config;
import l2s.gameserver.model.items.ItemInstance;

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
	protected final void writeImpl()
	{
		writeD(_objectId);
		writeD(_itemId);

		writeD(_x);
		writeD(_y);
		writeD(_z + Config.CLIENT_Z_SHIFT);
		writeD(_stackable);
		writeQ(_count);
		writeD(0x00); //c2
		writeC(_enchantLevel);
		writeC(0x00);
		writeC(_ensoulCount);
	}
}