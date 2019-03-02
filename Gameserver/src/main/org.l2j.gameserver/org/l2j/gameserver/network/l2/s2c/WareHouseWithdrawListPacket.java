package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInfo;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.model.items.Warehouse.ItemClassComparator;
import org.l2j.gameserver.model.items.Warehouse.WarehouseType;
import org.l2j.gameserver.network.l2.GameClient;

public class WareHouseWithdrawListPacket extends L2GameServerPacket
{
	private final int sendType;
	private long _adena;
	private List<ItemInfo> _itemList;
	private int _type;
	private int _inventoryUsedSlots;

	public WareHouseWithdrawListPacket(int sendType, Player player, WarehouseType type) {
		this.sendType = sendType;
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
	protected final void writeImpl(GameClient client, ByteBuffer buffer) {
		buffer.put((byte) sendType);
		if(sendType == 2) {
			buffer.putShort((short) 0x00);
			buffer.putInt(_inventoryUsedSlots);
			buffer.putInt(_itemList.size());

			for(ItemInfo item : _itemList) {
				writeItemInfo(buffer, item);
				buffer.putInt(item.getObjectId());
				buffer.putInt(0);
				buffer.putInt(0);
			}
		} else {
			buffer.putShort((short) _type);
			buffer.putLong(_adena);
			buffer.putInt(_inventoryUsedSlots);
			buffer.putInt(_itemList.size());
		}
	}
}