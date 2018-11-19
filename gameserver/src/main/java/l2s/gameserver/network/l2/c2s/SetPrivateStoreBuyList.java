package l2s.gameserver.network.l2.c2s;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import l2s.commons.math.SafeMath;
import l2s.gameserver.data.string.ItemNameHolder;
import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.TradeItem;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.PrivateStoreBuyManageList;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.utils.TradeHelper;

public class SetPrivateStoreBuyList extends L2GameClientPacket
{
	private List<BuyItemInfo> _items = Collections.emptyList();

	@Override
	protected void readImpl()
	{
		int count = readD();
		if(count * 40 > _buf.remaining() || count > Short.MAX_VALUE || count < 1)
			return;

		_items = new ArrayList<BuyItemInfo>();

		for(int i = 0; i < count; i++)
		{
			BuyItemInfo item = new BuyItemInfo();
			item.id = readD();
			item.enchant_level = readD();
			item.count = readQ();
			item.price = readQ();

			if(item.count < 1 || item.price < 1)
				break;

			readD();
			readD();

			readH();
			readH();
			readH();
			readH();
			readH();
			readH();
			readH();
			readH();
			readD();

			int saCount = readC();
			for(int s = 0; s < saCount; s++)
				readD();

			saCount = readC();
			for(int s = 0; s < saCount; s++)
				readD();

			_items.add(item);
		}
	}

	@Override
	protected void runImpl()
	{
		Player buyer = getClient().getActiveChar();
		if(buyer == null || _items.isEmpty())
			return;

		if(!TradeHelper.checksIfCanOpenStore(buyer, Player.STORE_PRIVATE_BUY))
		{
			buyer.sendActionFailed();
			return;
		}

		List<TradeItem> buyList = new CopyOnWriteArrayList<TradeItem>();
		long totalCost = 0;
		try
		{
			loop: for(BuyItemInfo i : _items)
			{
				ItemTemplate item = ItemHolder.getInstance().getTemplate(i.id);

				if(item == null || i.id == ItemTemplate.ITEM_ID_ADENA)
					continue;

				if(item.getReferencePrice() / 2 > i.price)
				{
					buyer.sendMessage(new CustomMessage("l2s.gameserver.network.l2.c2s.SetPrivateStoreBuyList.TooLowPrice").addString(ItemNameHolder.getInstance().getItemName(buyer, i.id)).addNumber(item.getReferencePrice() / 2));
					continue;
				}

				if(item.isStackable())
					for(TradeItem bi : buyList)
						if(bi.getItemId() == i.id)
						{
							bi.setOwnersPrice(i.price);
							bi.setCount(bi.getCount() + i.count);
							totalCost = SafeMath.addAndCheck(totalCost, SafeMath.mulAndCheck(i.count, i.price));
							continue loop;
						}

				TradeItem bi = new TradeItem();
				bi.setItemId(i.id);
				bi.setCount(i.count);
				bi.setOwnersPrice(i.price);
				bi.setEnchantLevel(i.enchant_level);
				totalCost = SafeMath.addAndCheck(totalCost, SafeMath.mulAndCheck(i.count, i.price));
				buyList.add(bi);
			}
		}
		catch(ArithmeticException ae)
		{
			//TODO audit
			buyer.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
			return;
		}

		if(buyList.size() > buyer.getTradeLimit())
		{
			buyer.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
			buyer.sendPacket(new PrivateStoreBuyManageList(buyer));
			return;
		}

		if(totalCost > buyer.getAdena())
		{
			buyer.sendPacket(SystemMsg.THE_PURCHASE_PRICE_IS_HIGHER_THAN_THE_AMOUNT_OF_MONEY_THAT_YOU_HAVE_AND_SO_YOU_CANNOT_OPEN_A_PERSONAL_STORE);
			buyer.sendPacket(new PrivateStoreBuyManageList(buyer));
			return;
		}

		if(!buyList.isEmpty())
		{
			buyer.setBuyList(buyList);
			buyer.setPrivateStoreType(Player.STORE_PRIVATE_BUY);
			buyer.storePrivateStore();
			buyer.broadcastPrivateStoreInfo();
			buyer.sitDown(null);
			buyer.broadcastCharInfo();
		}

		buyer.sendActionFailed();
	}

	private static class BuyItemInfo
	{
		public int id;
		public long count;
		public long price;
		public int enchant_level;
	}
}