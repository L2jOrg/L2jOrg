package org.l2j.gameserver.handler.items.impl;

import org.l2j.gameserver.model.Playable;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.s2c.ChooseInventoryItemPacket;

public class EnchantScrollItemHandler extends DefaultItemHandler {

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;
		Player player = (Player) playable;

		if(player.getEnchantScroll() != null)
			return false;

		player.setEnchantScroll(item);
		player.sendPacket(new ChooseInventoryItemPacket(item.getItemId()));
		return true;
	}
}