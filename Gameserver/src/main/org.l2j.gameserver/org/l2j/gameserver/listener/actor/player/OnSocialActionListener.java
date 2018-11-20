package org.l2j.gameserver.listener.actor.player;

import org.l2j.gameserver.listener.PlayerListener;
import org.l2j.gameserver.model.GameObject;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.c2s.RequestActionUse.Action;

/**
 * Listener for social actions performed by player
 * 
 * @author Yorie
 */
public interface OnSocialActionListener extends PlayerListener
{
	public void onSocialAction(Player player, GameObject target, Action action);
}