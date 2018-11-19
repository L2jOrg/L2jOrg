package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.utils.Location;

/**
 * 0000: 17  1a 95 20 48  9b da 12 40  44 17 02 00  03 f0 fc ff  98 f1 ff ff                                     .....
 * format  ddddd
 */
public class GetItemPacket extends L2GameServerPacket
{
	private int _playerId, _itemObjId;
	private Location _loc;

	public GetItemPacket(ItemInstance item, int playerId)
	{
		_itemObjId = item.getObjectId();
		_loc = item.getLoc();
		_playerId = playerId;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_playerId);
		writeD(_itemObjId);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
	}
}