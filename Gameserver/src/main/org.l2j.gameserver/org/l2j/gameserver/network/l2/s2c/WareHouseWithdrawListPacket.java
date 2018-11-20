package org.l2j.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInfo;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.model.items.Warehouse.ItemClassComparator;
import org.l2j.gameserver.model.items.Warehouse.WarehouseType;

public class WareHouseWithdrawListPacket extends L2GameServerPacket
{
	private long _adena;
	private List<ItemInfo> _itemList = new ArrayList<ItemInfo>();
	private int _type;
	private int _inventoryUsedSlots;

	public WareHouseWithdrawListPacket(Player player, WarehouseType type)
	{
		_adena = player.getAdena();
		_type = type.ordinal();

		ItemInstance[] items;
		switch(type)
		{
			case PRIVATE:
				items = player.getWarehouse().getItems();
				break;
			case FREIGHT:
				items = player.getFreight().getItems();
				break;
			case CLAN:
			case CASTLE:
				items = player.getClan().getWarehouse().getItems();
				break;
			default:
				_itemList = Collections.emptyList();
				return;
		}

		_itemList = new ArrayList<ItemInfo>(items.length);
		Arrays.sort(items, ItemClassComparator.getInstance());
		for(ItemInstance item : items)
			_itemList.add(new ItemInfo(item));

		_inventoryUsedSlots = player.getInventory().getSize();
	}

	@Override
	protected final void writeImpl()
	{
		writeH(_type);
		writeQ(_adena);
		writeH(_itemList.size());
		if(_type == 1 || _type == 2)
		{
			if(_itemList.size() > 0)
			{
				writeH(0x01);
				writeD(0x1063); // TODO: writeD(_itemList.get(0).getItemId()); первый предмет в списке.
			}
			else
				writeH(0x00);
		}
		writeD(_inventoryUsedSlots); //Количество занятых ячеек в инвентаре.
		for(ItemInfo item : _itemList)
		{
			writeItemInfo(item);
			writeD(item.getObjectId());
			writeD(0);
			writeD(0);
		}
	}
}