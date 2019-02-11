package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.model.items.TradeItem;

import io.github.joealisson.primitive.sets.IntSet;
import io.github.joealisson.primitive.sets.impl.HashIntSet;
import org.l2j.gameserver.network.l2.GameClient;

public class PrivateStoreBuyList extends L2GameServerPacket
{
	private final int _buyerId;
	private final long _adena;
	private List<TradeItem> _sellList;

	/**
	 * Список вещей в личном магазине покупки, показываемый продающему
	 * @param seller
	 * @param buyer
	 */
	public PrivateStoreBuyList(Player seller, Player buyer)
	{
		_adena = seller.getAdena();
		_buyerId = buyer.getObjectId();
		_sellList = new ArrayList<TradeItem>();

		ItemInstance[] items = seller.getInventory().getItems();
		IntSet addedItems = new HashIntSet();
		for(TradeItem bi : buyer.getBuyList())
		{
			TradeItem si = null;
			for(ItemInstance item : items)
				if(item.getItemId() == bi.getItemId() && item.canBePrivateStore(seller) && !addedItems.contains(item.getObjectId()))
				{
					if((!item.isArmor() && !item.isAccessory() && !item.isWeapon()) || item.getEnchantLevel() == bi.getEnchantLevel())
					{
						si = new TradeItem(item);
						si.setOwnersPrice(bi.getOwnersPrice());
						si.setCount(bi.getCount());
						si.setCurrentValue(Math.min(bi.getCount(), item.getCount()));
						addedItems.add(item.getObjectId());
						break;
					}
				}
			if(si == null)
			{
				si = new TradeItem();
				si.setItemId(bi.getItemId());
				si.setOwnersPrice(bi.getOwnersPrice());
				si.setCount(bi.getCount());
				si.setEnchantLevel(bi.getEnchantLevel());
				si.setCurrentValue(0);
			}
			_sellList.add(si);
		}
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_buyerId);
		buffer.putLong(_adena);
		buffer.putInt(70);
		buffer.putInt(_sellList.size());
		for(TradeItem si : _sellList)
		{
			writeItemInfo(buffer, si, si.getCurrentValue());
			buffer.putInt(si.getObjectId());
			buffer.putLong(si.getOwnersPrice());
			buffer.putLong(si.getStorePrice());
			buffer.putLong(si.getCount()); // maximum possible tradecount
		}
	}
}