package org.l2j.gameserver.templates.npc;

import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.model.items.TradeItem;

/**
 * @author Bonux
 **/
public final class BuyListTemplate
{
	private final int _listId;
	private final int _npcId;
	private final int _id;
	private final int _baseMarkup;
	private final List<TradeItem> _items = new ArrayList<TradeItem>();

	public BuyListTemplate(int npcId, int id, int baseMarkup)
	{
		_listId = hashCode();
		_npcId = npcId;
		_id = id;
		_baseMarkup = baseMarkup;
	}

	public int getListId()
	{
		return _listId;
	}

	public int getNpcId()
	{
		return _npcId;
	}

	public int getId()
	{
		return _id;
	}

	public int getBaseMarkup()
	{
		return _baseMarkup;
	}

	public void addItem(TradeItem item)
	{
		_items.add(item);
	}

	public synchronized List<TradeItem> getItems()
	{
		int currentTime = (int)(System.currentTimeMillis() / 60000L);

		List<TradeItem> result = new ArrayList<TradeItem>();
		for(TradeItem ti : _items)
		{
			// А не пора ли обновить количество лимитированных предметов в трейд листе?
			if(ti.isCountLimited())
			{
				if(ti.getCurrentValue() < ti.getCount() && ti.getLastRechargeTime() + ti.getRechargeTime() <= currentTime)
				{
					ti.setLastRechargeTime(currentTime);
					ti.setCurrentValue(ti.getCount());
				}

				if(ti.getCurrentValue() == 0)
					continue;
			}

			result.add(ti);
		}
		return result;
	}

	public TradeItem getItemByItemId(int itemId)
	{
		for(TradeItem item : _items)
		{
			if(item.getItemId() == itemId)
				return item;
		}
		return null;
	}

	public synchronized void updateItems(List<TradeItem> items)
	{
		for(TradeItem item : items)
		{
			TradeItem ti = getItemByItemId(item.getItemId());

			if(ti.isCountLimited())
				ti.setCurrentValue(Math.max(ti.getCurrentValue() - item.getCount(), 0));
		}
	}


	public void refresh()
	{
		for(TradeItem ti : _items)
		{
			if(ti.isCountLimited())
			{
				ti.setLastRechargeTime((int) (System.currentTimeMillis() / 60000L));
				ti.setCurrentValue(ti.getCount());
			}
		}
	}


	public BuyListTemplate clone()
	{
		BuyListTemplate template = new BuyListTemplate(getNpcId(), getId(), getBaseMarkup());
		for(TradeItem item : getItems())
			template.addItem(item.clone());
		return template;
	}
}