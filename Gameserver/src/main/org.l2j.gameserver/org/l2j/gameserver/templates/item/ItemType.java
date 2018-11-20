package org.l2j.gameserver.templates.item;

import org.l2j.gameserver.handler.items.IItemHandler;

public interface ItemType
{
	public long mask();

	public IItemHandler getHandler();

	public ExItemType getExType();
}