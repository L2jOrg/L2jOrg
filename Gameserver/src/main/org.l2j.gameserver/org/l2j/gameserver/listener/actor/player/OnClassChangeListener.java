package org.l2j.gameserver.listener.actor.player;

import org.l2j.gameserver.listener.PlayerListener;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.base.ClassId;

/**
 * @author Bonux
**/
public interface OnClassChangeListener extends PlayerListener
{
	public void onClassChange(Player player, ClassId oldClass, ClassId newClass);
}