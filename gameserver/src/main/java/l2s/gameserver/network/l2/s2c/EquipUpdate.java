package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.items.ItemInfo;
import l2s.gameserver.model.items.ItemInstance;

/**
 * 5e
 * 01 00 00 00 		01 - added ?  02 - modified
 * 7b 86 73 42      object id
 * 08 00 00 00      body slot
 *
 * body slot
 * 0000  ?? underwear
 * 0001  ear
 * 0002  ear
 * 0003  neck
 * 0004  finger   (magic ring)
 * 0005  finger   (magic ring)
 * 0006  head     (l.cap)
 * 0007  r.hand   (dagger)
 * 0008  l.hand   (arrows)
 * 0009  hands    (int gloves)
 * 000a  chest    (squire shirt)
 * 000b  legs     (squire pants)
 * 000c  feet
 * 000d  ?? back
 * 000e  lr.hand   (bow)
 *
 * format  ddd
 */
//@Deprecated
public class EquipUpdate extends L2GameServerPacket
{
	private ItemInfo _item;

	public EquipUpdate(ItemInstance item, int change)
	{
		_item = new ItemInfo(item);
		_item.setLastChange(change);
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_item.getLastChange());
		writeD(_item.getObjectId());
		writeD(_item.getEquipSlot());
	}
}