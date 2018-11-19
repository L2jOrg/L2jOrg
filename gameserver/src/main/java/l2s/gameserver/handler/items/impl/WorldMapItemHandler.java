package l2s.gameserver.handler.items.impl;

import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.s2c.ShowMinimapPacket;

public class WorldMapItemHandler extends DefaultItemHandler
{
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