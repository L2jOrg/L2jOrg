package l2s.gameserver.templates.item;

import l2s.gameserver.handler.items.IItemHandler;

public interface ItemType
{
	public long mask();

	public IItemHandler getHandler();

	public ExItemType getExType();
}