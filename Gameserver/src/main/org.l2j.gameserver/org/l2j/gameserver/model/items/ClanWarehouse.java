package org.l2j.gameserver.model.items;

import org.l2j.gameserver.model.items.ItemInstance.ItemLocation;
import org.l2j.gameserver.model.pledge.Clan;

public final class ClanWarehouse extends Warehouse
{
	public ClanWarehouse(Clan clan)
	{
		super(clan.getClanId());
	}

	@Override
	public ItemLocation getItemLocation()
	{
		return ItemLocation.CLANWH;
	}
}