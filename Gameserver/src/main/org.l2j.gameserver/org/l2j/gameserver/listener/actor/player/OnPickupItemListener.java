package org.l2j.gameserver.listener.actor.player;

import org.l2j.gameserver.listener.PlayerListener;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;

/**
 * @author Bonux
**/
public interface OnPickupItemListener extends PlayerListener
{
	public void onPickupItem(Player player, ItemInstance item);
}