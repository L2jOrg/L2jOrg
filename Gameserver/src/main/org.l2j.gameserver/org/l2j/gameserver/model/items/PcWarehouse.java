package org.l2j.gameserver.model.items;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance.ItemLocation;

public class PcWarehouse extends Warehouse
{
	public PcWarehouse(Player owner)
	{
		super(owner.getObjectId());
	}

	public PcWarehouse(int ownerId)
	{
		super(ownerId);
	}

	@Override
	public ItemLocation getItemLocation()
	{
		return ItemLocation.WAREHOUSE;
	}
}