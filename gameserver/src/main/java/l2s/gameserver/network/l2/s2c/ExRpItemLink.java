package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.items.ItemInfo;

/**
 * ddQhdhhhhhdhhhhhhhh - Gracia Final
 */
public class ExRpItemLink extends L2GameServerPacket
{
	private ItemInfo _item;

	public ExRpItemLink(ItemInfo item)
	{
		_item = item;
	}

	@Override
	protected final void writeImpl()
	{
		writeItemInfo(_item);
	}
}