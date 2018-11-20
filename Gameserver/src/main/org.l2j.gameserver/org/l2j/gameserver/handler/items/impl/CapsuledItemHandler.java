package org.l2j.gameserver.handler.items.impl;

import java.util.List;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.Playable;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;
import org.l2j.gameserver.templates.item.data.CapsuledItemData;
import org.l2j.gameserver.utils.ItemFunctions;

/**
 * @author Bonux
 */
public class CapsuledItemHandler extends DefaultItemHandler
{
	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		Player player;
		if(playable.isPlayer())
			player = (Player) playable;
		else if(playable.isPet())
			player = playable.getPlayer();
		else
			return false;

		int itemId = item.getItemId();

		if(!canBeExtracted(player, item))
			return false;

		if(!reduceItem(player, item))
			return false;

		//sendUseMessage(player, item); На оффе не посылается.

		List<CapsuledItemData> capsuled_items = item.getTemplate().getCapsuledItems();
		for(CapsuledItemData ci : capsuled_items)
		{
			if(Rnd.chance(ci.getChance()))
			{
				long count;
				long minCount = ci.getMinCount();
				long maxCount = ci.getMaxCount();
				if(minCount == maxCount)
					count = minCount;
				else
					count = Rnd.get(minCount, maxCount);
				ItemFunctions.addItem(player, ci.getId(), count, ci.getEnchantLevel(), true);
			}
		}

		player.sendPacket(SystemMessagePacket.removeItems(itemId, 1));
		return true;
	}
}