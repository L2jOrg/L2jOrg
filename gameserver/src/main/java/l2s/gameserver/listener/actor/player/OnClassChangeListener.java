package l2s.gameserver.listener.actor.player;

import l2s.gameserver.listener.PlayerListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassId;

/**
 * @author Bonux
**/
public interface OnClassChangeListener extends PlayerListener
{
	public void onClassChange(Player player, ClassId oldClass, ClassId newClass);
}