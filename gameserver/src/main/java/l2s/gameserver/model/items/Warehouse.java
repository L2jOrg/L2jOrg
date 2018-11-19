package l2s.gameserver.model.items;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import l2s.commons.dao.JdbcEntityState;
import l2s.gameserver.model.items.ItemInstance.ItemLocation;
import l2s.gameserver.templates.item.ItemTemplate;

public abstract class Warehouse extends ItemContainer
{
	public static enum WarehouseType
	{
		NONE,
		PRIVATE,
		CLAN,
		CASTLE,
		FREIGHT
	}

	public static class ItemClassComparator implements Comparator<ItemInstance>
	{
		private static final Comparator<ItemInstance> instance = new ItemClassComparator();

		public static final Comparator<ItemInstance> getInstance()
		{
			return instance;
		}

		@Override
		public int compare(ItemInstance o1, ItemInstance o2)
		{
			if(o1 == null || o2 == null)
				return 0;
			int diff = o1.getExType().mask() - o2.getExType().mask();
			if(diff == 0)
				diff = o1.getGrade().ordinal() - o2.getGrade().ordinal();
			if(diff == 0)
				diff = o1.getItemId() - o2.getItemId();
			if(diff == 0)
				diff = o1.getEnchantLevel() - o2.getEnchantLevel();
			return diff;
		}
	}

	protected final int _ownerId;

	protected Warehouse(int ownerId)
	{
		_ownerId = ownerId;
	}

	public int getOwnerId()
	{
		return _ownerId;
	}

	public abstract ItemLocation getItemLocation();

	public ItemInstance[] getItems()
	{
		List<ItemInstance> result = new ArrayList<ItemInstance>();

		readLock();
		try
		{
			for(ItemInstance item : _items)
				result.add(item);
		}
		finally
		{
			readUnlock();
		}

		return result.toArray(new ItemInstance[result.size()]);
	}

	public long getCountOfAdena()
	{
		return getCountOf(ItemTemplate.ITEM_ID_ADENA);
	}

	@Override
	protected void onAddItem(ItemInstance item)
	{
		item.setOwnerId(getOwnerId());
		item.setLocation(getItemLocation());
		item.setLocData(0);
		if(item.getJdbcState().isSavable())
		{
			item.save();
		}
		else
		{
			item.setJdbcState(JdbcEntityState.UPDATED);
			item.update();
		}
	}

	@Override
	protected void onModifyItem(ItemInstance item)
	{
		item.setJdbcState(JdbcEntityState.UPDATED);
		item.update();
	}

	@Override
	protected void onRemoveItem(ItemInstance item)
	{
		item.setLocData(-1);
	}

	@Override
	protected void onDestroyItem(ItemInstance item)
	{
		item.setCount(0L);
		item.delete();
	}

	public void restore()
	{
		final int ownerId = getOwnerId();

		writeLock();
		try
		{
			Collection<ItemInstance> items = _itemsDAO.getItemsByOwnerIdAndLoc(ownerId, getItemLocation());

			for(ItemInstance item : items)
				_items.add(item);
		}
		finally
		{
			writeUnlock();
		}
	}
}