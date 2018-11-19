package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.items.ItemInfo;

public class TradeUpdatePacket extends L2GameServerPacket
{
	private ItemInfo _item;
	private long _amount;

	public TradeUpdatePacket(ItemInfo item, long amount)
	{
		_item = item;
		_amount = amount;
	}

	@Override
	protected final void writeImpl()
	{
		writeH(1);
		writeH(_amount > 0 && _item.getItem().isStackable() ? 3 : 2);
		writeItemInfo(_item, _amount);
	}
}