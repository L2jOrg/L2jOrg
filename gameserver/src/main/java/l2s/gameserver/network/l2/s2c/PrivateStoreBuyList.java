package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.TradeItem;

import org.napile.primitive.sets.IntSet;
import org.napile.primitive.sets.impl.HashIntSet;

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
	protected final void writeImpl()
	{
		writeD(_buyerId);
		writeQ(_adena);
		writeD(70);
		writeD(_sellList.size());
		for(TradeItem si : _sellList)
		{
			writeItemInfo(si, si.getCurrentValue());
			writeD(si.getObjectId());
			writeQ(si.getOwnersPrice());
			writeQ(si.getStorePrice());
			writeQ(si.getCount()); // maximum possible tradecount
		}
	}
}