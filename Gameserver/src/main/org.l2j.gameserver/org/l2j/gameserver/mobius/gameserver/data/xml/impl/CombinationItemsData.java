/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.data.xml.impl;

import org.l2j.commons.util.IGameXmlReader;
import org.l2j.gameserver.mobius.gameserver.datatables.ItemTable;
import org.l2j.gameserver.mobius.gameserver.model.StatsSet;
import org.l2j.gameserver.mobius.gameserver.model.items.combination.CombinationItem;
import org.l2j.gameserver.mobius.gameserver.model.items.combination.CombinationItemReward;
import org.l2j.gameserver.mobius.gameserver.model.items.combination.CombinationItemType;
import org.w3c.dom.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author UnAfraid
 */
public class CombinationItemsData implements IGameXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(CombinationItemsData.class.getName());
	private final List<CombinationItem> _items = new ArrayList<>();
	
	protected CombinationItemsData()
	{
		load();
	}
	
	@Override
	public synchronized void load()
	{
		_items.clear();
		parseDatapackFile("data/CombinationItems.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _items.size() + " combinations.");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "item", itemNode ->
		{
			final CombinationItem item = new CombinationItem(new StatsSet(parseAttributes(itemNode)));
			
			forEach(itemNode, "reward", rewardNode ->
			{
				final int id = parseInteger(rewardNode.getAttributes(), "id");
				final int count = parseInteger(rewardNode.getAttributes(), "count", 1);
				final CombinationItemType type = parseEnum(rewardNode.getAttributes(), CombinationItemType.class, "type");
				item.addReward(new CombinationItemReward(id, count, type));
				if (ItemTable.getInstance().getTemplate(id) == null)
				{
					LOGGER.info(getClass().getSimpleName() + ": Could not find item with id " + id);
				}
			});
			_items.add(item);
		}));
	}
	
	public int getLoadedElementsCount()
	{
		return _items.size();
	}
	
	public List<CombinationItem> getItems()
	{
		return _items;
	}
	
	public CombinationItem getItemsBySlots(int firstSlot, int secondSlot)
	{
		return _items.stream().filter(item -> (item.getItemOne() == firstSlot) && (item.getItemTwo() == secondSlot)).findFirst().orElse(null);
	}
	
	public List<CombinationItem> getItemsByFirstSlot(int id)
	{
		return _items.stream().filter(item -> item.getItemOne() == id).collect(Collectors.toList());
	}
	
	public List<CombinationItem> getItemsBySecondSlot(int id)
	{
		return _items.stream().filter(item -> item.getItemTwo() == id).collect(Collectors.toList());
	}
	
	public static final CombinationItemsData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final CombinationItemsData INSTANCE = new CombinationItemsData();
	}
}