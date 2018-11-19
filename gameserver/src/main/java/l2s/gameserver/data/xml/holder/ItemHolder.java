package l2s.gameserver.data.xml.holder;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import l2s.commons.data.xml.AbstractHolder;
import l2s.commons.lang.ArrayUtils;
import l2s.gameserver.templates.item.ItemTemplate;

public final class ItemHolder extends AbstractHolder
{
	private static final ItemHolder _instance = new ItemHolder();

	private final TIntObjectMap<ItemTemplate> _items = new TIntObjectHashMap<ItemTemplate>();
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

		for(int id : _items.keys())
			if(id > highestId)
				highestId = id;

		_allTemplates = new ItemTemplate[highestId + 1];

		for(TIntObjectIterator<ItemTemplate> iterator = _items.iterator(); iterator.hasNext();)
		{
			iterator.advance();
			_allTemplates[iterator.key()] = iterator.value();
		}
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
			warn("Not defined item id : " + id + ", or out of range!", new Exception());
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