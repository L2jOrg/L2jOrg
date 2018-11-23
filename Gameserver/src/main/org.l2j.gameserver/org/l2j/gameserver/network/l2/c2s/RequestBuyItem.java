package org.l2j.gameserver.network.l2.c2s;

import java.util.ArrayList;
import java.util.List;

import org.l2j.commons.math.SafeMath;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.holder.BuyListHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.residence.Castle;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.model.items.Inventory;
import org.l2j.gameserver.model.items.TradeItem;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.ExBuySellListPacket;
import org.l2j.gameserver.templates.npc.BuyListTemplate;
import org.l2j.gameserver.utils.NpcUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * format:		cddb, b - array of (dd)
 */
public class RequestBuyItem extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(RequestBuyItem.class);

	private int _listId;
	private int _count;
	private int[] _items;
	private long[] _itemQ;

	@Override
	protected void readImpl()
	{
		_listId = readInt();
		_count = readInt();
		if(_count * 12 > availableData() || _count > Short.MAX_VALUE || _count < 1)
		{
			_count = 0;
			return;
		}

		_items = new int[_count];
		_itemQ = new long[_count];

		for(int i = 0; i < _count; i++)
		{
			_items[i] = readInt();
			_itemQ[i] = readLong();
			if(_itemQ[i] < 1)
			{
				_count = 0;
				break;
			}
		}
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null || _count == 0)
			return;

		// Проверяем, не подменили ли id
		if(activeChar.getBuyListId() != _listId)
			//TODO audit
			return;

		if(activeChar.isActionsDisabled())
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isInStoreMode())
		{
			activeChar.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
			return;
		}

		if(activeChar.isInTrade())
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isFishing())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
			return;
		}

		if(activeChar.isInTrainingCamp())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_TAKE_OTHER_ACTION_WHILE_ENTERING_THE_TRAINING_CAMP);
			return;
		}

		if(!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && activeChar.isPK() && !activeChar.isGM())
		{
			activeChar.sendActionFailed();
			return;
		}

		BuyListTemplate list = null;

		NpcInstance merchant = NpcUtils.canPassPacket(activeChar, this);
		if(merchant != null)
			list = merchant.getBuyList(_listId);

		if(activeChar.isGM() && (merchant == null || list == null || merchant.getNpcId() != list.getNpcId()))
			list = BuyListHolder.getInstance().getBuyList(_listId);

		if(list == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		int slots = 0;
		long weight = 0;
		long totalPrice = 0;
		long tax = 0;
		double buyTaxRate = 0;
		double sellTaxRate = 0;

		Castle castle = null;
		if(merchant != null)
		{
			castle = merchant.getCastle(activeChar);
			if(castle != null)
			{
				buyTaxRate = castle.getBuyTaxRate();
				sellTaxRate = castle.getSellTaxRate();
			}
		}

		List<TradeItem> buyList = new ArrayList<TradeItem>(_count);
		List<TradeItem> tradeList = list.getItems();

		Inventory inventory = activeChar.getInventory();

		inventory.writeLock();
		activeChar.getRefund().writeLock();
		try
		{
			loop: for(int i = 0; i < _count; i++)
			{
				int itemId = _items[i];
				long count = _itemQ[i];
				long price = 0;
				boolean limited = false;

				for(TradeItem ti : tradeList)
					if(ti.getItemId() == itemId)
					{
						if(ti.isCountLimited() && ti.getCurrentValue() < count)
							continue loop;
						limited = ti.isCountLimited();
						price = ti.getOwnersPrice();
					}

				if(!limited && price == 0 && (!activeChar.isGM() || !activeChar.getPlayerAccess().UseGMShop))
				{
					//TODO audit
					activeChar.sendActionFailed();
					return;
				}

				totalPrice = SafeMath.addAndCheck(totalPrice, SafeMath.mulAndCheck(count, price));

				TradeItem ti = new TradeItem();
				ti.setItemId(itemId);
				ti.setCount(count);
				ti.setOwnersPrice(price);

				weight = SafeMath.addAndCheck(weight, SafeMath.mulAndCheck(count, ti.getItem().getWeight()));
				if(!ti.getItem().isStackable() || inventory.getItemByItemId(itemId) == null)
					slots++;

				buyList.add(ti);
			}

			tax = (long) (totalPrice * sellTaxRate);

			totalPrice = SafeMath.addAndCheck(totalPrice, tax);

			if(!inventory.validateWeight(weight))
			{
				activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
				return;
			}

			if(!inventory.validateCapacity(slots))
			{
				activeChar.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
				return;
			}

			if(!activeChar.reduceAdena(totalPrice))
			{
				activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}

			for(TradeItem ti : buyList)
				inventory.addItem(ti.getItemId(), ti.getCount());

			// Для магазинов с ограниченным количеством товара число продаваемых предметов уменьшаем после всех проверок
			list.updateItems(buyList);

			// Add tax to castle treasury if not owned by npc clan
			if(castle != null)
				if(tax > 0 && castle.getOwnerId() > 0 && activeChar.getReflection().isMain())
					castle.addToTreasury(tax, true);
		}
		catch(ArithmeticException ae)
		{
			//TODO audit
			activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
			return;
		}
		finally
		{
			inventory.writeUnlock();
			activeChar.getRefund().writeUnlock();
		}

		sendPacket(new ExBuySellListPacket.SellRefundList(activeChar, true, buyTaxRate));
		activeChar.sendChanges();
	}
}