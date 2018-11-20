package org.l2j.gameserver.listener.actor.player;

import org.l2j.gameserver.listener.PlayerListener;
import org.l2j.gameserver.model.Player;

public interface OnPlayerClanInviteListener extends PlayerListener
{
	public void onClanInvite(Player player);
}