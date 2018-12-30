package org.l2j.gameserver.data.xml.holder;

import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.templates.npc.BuyListTemplate;
import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Bonux
**/
public final class BuyListHolder extends AbstractHolder
{
	private static final BuyListHolder _instance = new BuyListHolder();

	private final IntObjectMap<BuyListTemplate> _buyListsByListId = new HashIntObjectMap<BuyListTemplate>();
	private final IntObjectMap<IntObjectMap<BuyListTemplate>> _buyListsByNpcId = new HashIntObjectMap<>();

	public static BuyListHolder getInstance()
	{
		return _instance;
	}

	public void addBuyList(BuyListTemplate buyList)
	{
		_buyListsByListId.put(buyList.getListId(), buyList);

		IntObjectMap<BuyListTemplate> buyLists = _buyListsByNpcId.get(buyList.getNpcId());
		if(buyLists == null)
		{
			buyLists = new HashIntObjectMap<BuyListTemplate>();
			_buyListsByNpcId.put(buyList.getNpcId(), buyLists);
		}

		buyLists.put(buyList.getId(), buyList);
	}

	public BuyListTemplate getBuyList(int listId)
	{
		return _buyListsByListId.get(listId);
	}

	public Collection<BuyListTemplate> getBuyLists(int npcId)
	{
		IntObjectMap<BuyListTemplate> buyLists = _buyListsByNpcId.get(npcId);
		if(buyLists == null)
			return Collections.emptyList();

		return buyLists.values();
	}

	public BuyListTemplate getBuyList(int npcId, int buyListId)
	{
		IntObjectMap<BuyListTemplate> buyLists = _buyListsByNpcId.get(npcId);
		if(buyLists == null)
			return null;

		return buyLists.get(buyListId);
	}

	@Override
	public int size()
	{
		return _buyListsByListId.size();
	}

	@Override
	public void clear()
	{
		_buyListsByListId.clear();
		_buyListsByNpcId.clear();
	}
}