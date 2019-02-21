package org.l2j.gameserver.handler.items.impl;

import org.l2j.gameserver.model.Playable;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.s2c.ShowMinimapPacket;

public class WorldMapItemHandler extends DefaultItemHandler {

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;

		Player player = (Player) playable;
		player.sendPacket(new ShowMinimapPacket(player, item.getItemId()));
		return true;
	}
}