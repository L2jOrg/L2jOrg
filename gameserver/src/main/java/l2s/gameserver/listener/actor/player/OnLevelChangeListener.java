package l2s.gameserver.listener.actor.player;

import l2s.gameserver.listener.PlayerListener;
import l2s.gameserver.model.Player;

/**
 * @author : Ragnarok
 * @date : 28.03.12  16:54
 */
public interface OnLevelChangeListener extends PlayerListener
{
	public void onLevelChange(Player player, int oldLvl, int newLvl);
}