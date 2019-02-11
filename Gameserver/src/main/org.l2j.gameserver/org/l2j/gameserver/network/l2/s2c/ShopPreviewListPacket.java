package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInfo;
import org.l2j.gameserver.model.items.TradeItem;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.templates.item.ItemTemplate;
import org.l2j.gameserver.templates.npc.BuyListTemplate;

public class ShopPreviewListPacket extends L2GameServerPacket
{
	private final int _listId;
	private final List<ItemInfo> _itemList;
	private final long _money;

	public ShopPreviewListPacket(BuyListTemplate list, Player player)
	{
		_listId = list.getListId();
		_money = player.getAdena();
		List<TradeItem> tradeList = list.getItems();
		_itemList = new ArrayList<ItemInfo>(tradeList.size());
		for(TradeItem item : tradeList)
		{
			if(item.getItem().isEquipable() && (item.getItem().isArmor() || item.getItem().isWeapon())) // TODO: [Bonux] Проверить.
				_itemList.add(item);
		}
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(0x13c0); //?
		buffer.putLong(_money);
		buffer.putInt(_listId);
		buffer.putShort((short) _itemList.size());

		for(ItemInfo item : _itemList)
			if(item.getItem().isEquipable())
			{
				buffer.putInt(item.getItemId());
				buffer.putShort((short) item.getItem().getType2()); // item type2
				buffer.putShort((short) (item.getItem().isEquipable() ? item.getItem().getBodyPart() : 0x00));
				buffer.putLong(getWearPrice(item.getItem()));
			}
	}

	public static int getWearPrice(ItemTemplate item)
	{
		switch(item.getGrade())
		{
			case D:
				return 50;
			case C:
				return 100;
				//TODO: Не известно сколько на оффе стоит примерка B - S84 ранга.
			case B:
				return 200;
			case A:
				return 500;
			case S:
				return 1000;
			case S80:
				return 2000;
				/*case S84:
					return 2500;*/
			default:
				return 10;
		}
	}
}