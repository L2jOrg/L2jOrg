package handler.items;

import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessage;

abstract class SimpleItemHandler extends ScriptItemHandler
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

		if(player.isInFlyingTransform())
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
			return false;
		}

		return useItemImpl(player, item, ctrl);
	}

	protected abstract boolean useItemImpl(Player player, ItemInstance item, boolean ctrl);
}
