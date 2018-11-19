package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.Config;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.utils.Location;

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
		writeD(_playerId);
		writeD(item_obj_id);
		writeD(item_id);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z + Config.CLIENT_Z_SHIFT);
		writeC(_stackable);
		writeQ(_count);
		writeC(1); // unknown
		writeC(_enchantLevel);
		writeC(0);
		writeC(_ensoulCount);
	}
}