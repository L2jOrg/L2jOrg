package org.l2j.gameserver.listener.actor.player;

import org.l2j.gameserver.listener.PlayerListener;
import org.l2j.gameserver.model.Player;

public interface OnPlayerEnterListener extends PlayerListener
{
	public void onPlayerEnter(Player player);
}