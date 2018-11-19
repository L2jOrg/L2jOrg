package l2s.gameserver.model.items;

import l2s.commons.dao.JdbcEntityState;
import l2s.gameserver.model.Player;

/**
 * @author VISTALL
 * @date 10:10/02.02.2011
 */
public class PcRefund extends ItemContainer
{
	public PcRefund(Player player)
	{

	}

	@Override
	protected void onAddItem(ItemInstance item)
	{
		item.setLocation(ItemInstance.ItemLocation.VOID);
		if(item.getJdbcState().isPersisted())
		{
			item.setJdbcState(JdbcEntityState.UPDATED);
			item.update();
		}

		if(_items.size() > 12) //FIXME [G1ta0] хардкод, достойны конфига
		{
			destroyItem(_items.remove(0));
		}
	}

	@Override
	protected void onModifyItem(ItemInstance item)
	{

	}

	@Override
	protected void onRemoveItem(ItemInstance item)
	{

	}

	@Override
	protected void onDestroyItem(ItemInstance item)
	{
		item.setCount(0);
		item.delete();
	}

	@Override
	public void clear()
	{
		writeLock();
		try
		{
			_itemsDAO.delete(_items);
			_items.clear();
		}
		finally
		{
			writeUnlock();
		}
	}
}
