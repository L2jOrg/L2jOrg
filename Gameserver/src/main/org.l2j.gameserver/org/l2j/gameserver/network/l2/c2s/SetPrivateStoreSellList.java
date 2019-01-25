package org.l2j.gameserver.network.l2.c2s;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.l2j.commons.lang.ArrayUtils;
import org.l2j.gameserver.Contants;
import org.l2j.gameserver.Contants.Items;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.model.items.TradeItem;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.PrivateStoreManageList;
import org.l2j.gameserver.templates.item.ItemTemplate;
import org.l2j.gameserver.utils.TradeHelper;

/**
 * Это список вещей которые игрок хочет продать в создаваемом им приватном магазине
 */
public class SetPrivateStoreSellList extends L2GameClientPacket
{
	private int _count;
	private boolean _package;
	private int[] _items; // objectId
	private long[] _itemQ; // count
	private long[] _itemP; // price

	@Override
	protected void readImpl()
	{
		_package = readInt() == 1;
		_count = readInt();
		// Иначе нехватит памяти при создании массива.
		if(_count * 20 > availableData() || _count > Short.MAX_VALUE || _count < 1)
		{
			_count = 0;
			return;
		}

		_items = new int[_count];
		_itemQ = new long[_count];
		_itemP = new long[_count];

		for(int i = 0; i < _count; i++)
		{
			_items[i] = readInt();
			_itemQ[i] = readLong();
			_itemP[i] = readLong();
			if(_itemQ[i] < 1 || _itemP[i] < 0 || ArrayUtils.indexOf(_items, _items[i]) < i)
			{
				_count = 0;
				break;
			}
		}
	}

	@Override
	protected void runImpl()
	{
		Player seller = getClient().getActiveChar();
		if(seller == null || _count == 0)
			return;

		if(!TradeHelper.checksIfCanOpenStore(seller, _package ? Player.STORE_PRIVATE_SELL_PACKAGE : Player.STORE_PRIVATE_SELL))
		{
			seller.sendActionFailed();
			return;
		}

		TradeItem temp;
		List<TradeItem> sellList = new CopyOnWriteArrayList<TradeItem>();

		seller.getInventory().writeLock();
		try
		{
			for(int i = 0; i < _count; i++)
			{
				int objectId = _items[i];
				long count = _itemQ[i];
				long price = _itemP[i];
				ItemInstance item = seller.getInventory().getItemByObjectId(objectId);

				if(item == null || item.getCount() < count || !item.canBePrivateStore(seller) || item.getItemId() == Items.ADENA)
					continue;
				if(item.getPriceLimitForItem() != 0 && price > item.getPriceLimitForItem())
					price = item.getPriceLimitForItem();
				temp = new TradeItem(item);
				temp.setCount(count);
				temp.setOwnersPrice(price);

				sellList.add(temp);
			}
		}
		finally
		{
			seller.getInventory().writeUnlock();
		}

		if(sellList.size() > seller.getTradeLimit())
		{
			seller.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
			seller.sendPacket(new PrivateStoreManageList(seller, _package));
			return;
		}

		if(!sellList.isEmpty())
		{
			seller.setSellList(_package, sellList);
			seller.setPrivateStoreType(_package ? Player.STORE_PRIVATE_SELL_PACKAGE : Player.STORE_PRIVATE_SELL);
			seller.storePrivateStore();
			seller.broadcastPrivateStoreInfo();
			seller.sitDown(null);
			seller.broadcastCharInfo();
		}

		seller.sendActionFailed();
	}
}