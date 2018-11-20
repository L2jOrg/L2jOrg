package org.l2j.gameserver.listener.actor;

import org.l2j.gameserver.listener.CharListener;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.utils.Location;

public interface OnMoveListener extends CharListener
{
	public void onMove(Creature actor, Location loc);
}