package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;

public class GMViewWarehouseWithdrawListPacket extends L2GameServerPacket
{
	private final ItemInstance[] _items;
	private String _charName;
	private long _charAdena;

	public GMViewWarehouseWithdrawListPacket(Player cha)
	{
		_charName = cha.getName();
		_charAdena = cha.getWarehouse().getAdena();
		_items = cha.getWarehouse().getItems();
	}

	@Override
	protected final void writeImpl()
	{
		writeS(_charName);
		writeQ(_charAdena);
		writeH(_items.length);
		for(ItemInstance temp : _items)
		{
			writeItemInfo(temp);
			writeD(temp.getObjectId());
		}
	}
}