package org.l2j.gameserver.data.xml.holder;

import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.commons.lang.ArrayUtils;
import org.l2j.gameserver.templates.item.ItemTemplate;
import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;

public final class ItemHolder extends AbstractHolder
{
	private static final ItemHolder _instance = new ItemHolder();

	private final IntObjectMap<ItemTemplate> _items = new HashIntObjectMap<ItemTemplate>();
	private ItemTemplate[] _allTemplates;

	public static ItemHolder getInstance()
	{
		return _instance;
	}

	private ItemHolder()
	{
	}

	public void addItem(ItemTemplate template)
	{
		_items.put(template.getItemId(), template);
	}

	private void buildFastLookupTable()
	{
		int highestId = 0;

		for(int id : _items.keySet().toArray())
			if(id > highestId)
				highestId = id;

		_allTemplates = new ItemTemplate[highestId + 1];

		_items.entrySet().forEach(entry -> {
			_allTemplates[entry.getKey()] = entry.getValue();
		});
	}

	/**
	 * Returns the item corresponding to the item ID
	 * @param id : int designating the item
	 */
	public ItemTemplate getTemplate(int id)
	{
		ItemTemplate item = ArrayUtils.valid(_allTemplates, id);
		if(item == null)
		{
			logger.warn("Not defined item id : " + id + ", or out of range!", new Exception());
			return null;
		}
		return _allTemplates[id];
	}

	public ItemTemplate[] getAllTemplates()
	{
		return _allTemplates;
	}

	@Override
	protected void process()
	{
		buildFastLookupTable();
	}

	@Override
	public int size()
	{
		return _items.size();
	}

	@Override
	public void clear()
	{
		_items.clear();
	}
}