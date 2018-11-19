package l2s.gameserver.listener.actor.player;

import l2s.gameserver.listener.PlayerListener;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.c2s.RequestActionUse.Action;

/**
 * Listener for social actions performed by player
 * 
 * @author Yorie
 */
public interface OnSocialActionListener extends PlayerListener
{
	public void onSocialAction(Player player, GameObject target, Action action);
}