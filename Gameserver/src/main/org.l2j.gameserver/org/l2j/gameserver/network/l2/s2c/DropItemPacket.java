package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.utils.Location;

public class DropItemPacket extends L2GameServerPacket
{
	private final Location _loc;
	private final int _playerId, item_obj_id, item_id, _stackable;
	private final long _count;
	private final int _enchantLevel, _ensoulCount;

	/**
	 * Constructor<?> of the DropItem server packet
	 * @param item : L2ItemInstance designating the item
	 * @param playerId : int designating the player ID who dropped the item
	 */
	public DropItemPacket(ItemInstance item, int playerId)
	{
		_playerId = playerId;
		item_obj_id = item.getObjectId();
		item_id = item.getItemId();
		_loc = item.getLoc();
		_stackable = item.isStackable() ? 1 : 0;
		_count = item.getCount();
		_enchantLevel = item.getEnchantLevel();
		_ensoulCount = item.getNormalEnsouls().length + item.getSpecialEnsouls().length;
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_playerId);
		writeInt(item_obj_id);
		writeInt(item_id);
		writeInt(_loc.x);
		writeInt(_loc.y);
		writeInt(_loc.z + Config.CLIENT_Z_SHIFT);
		writeByte(_stackable);
		writeLong(_count);
		writeByte(1); // unknown
		writeByte(_enchantLevel);
		writeByte(0);
		writeByte(_ensoulCount);
	}
}